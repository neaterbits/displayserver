package com.neaterbits.displayserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.events.common.EventSource;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;
import com.neaterbits.displayserver.io.common.Client;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.layers.Region;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.OpCodes;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.MapState;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.ProtocolException;
import com.neaterbits.displayserver.protocol.logging.XWindowsProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.DEPTH;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.FORMAT;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.VISUALTYPE;
import com.neaterbits.displayserver.protocol.messages.replies.AllocColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetPropertyReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetWindowAttributesReply;
import com.neaterbits.displayserver.protocol.messages.replies.InternAtomReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryResponseReply;
import com.neaterbits.displayserver.protocol.messages.requests.AllocColor;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.GetProperty;
import com.neaterbits.displayserver.protocol.messages.requests.GetSelectionOwner;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.GrabServer;
import com.neaterbits.displayserver.protocol.messages.requests.InternAtom;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.QueryExtension;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;
import com.neaterbits.displayserver.server.XWindowsConnectionState.State;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.Screen;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowEventListener;

public class XWindowsProtocolServer implements AutoCloseable {

    private final XWindowsProtocolLog protocolLog;
    private final NonBlockingChannelWriterLog connectionWriteLog;
    
	private final Display display;
	private final ServerResourceIdAllocator resourceIdAllocator;
	private final Atoms atoms;
	
	private final List<XWindowsScreen> screens;
	private final Map<Window, XWindowsConnectionState> connectionByWindow;
	
	public XWindowsProtocolServer(
	        EventSource driverEventSource,
	        GraphicsDriver graphicsDriver,
	        XWindowsProtocolLog protocolLog,
	        NonBlockingChannelWriterLog connectionWriteLog) throws IOException {
		
		Objects.requireNonNull(graphicsDriver);
		
		this.protocolLog = protocolLog;
		this.connectionWriteLog = connectionWriteLog;
		
		this.resourceIdAllocator = new ServerResourceIdAllocator();
		
		this.atoms = new Atoms();
		
		this.connectionByWindow = new HashMap<>();

		this.screens = getScreens(graphicsDriver);
		
		this.display = new Display(screens.stream()
		        .map(screen -> screen.getScreen())
		        .collect(Collectors.toList()));
	}

	public Client processConnection(SocketChannel socketChannel) {
	    return new ConnectionState(XWindowsProtocolServer.this, socketChannel, connectionWriteLog) {

            @Override
            public Integer getLengthOfMessage(ByteBuffer byteBuffer) {

                final Integer length;
                
                if (getState() == State.CREATED) {
                    
                    if (byteBuffer.get(byteBuffer.position()) == 0x6C) {
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    }
                    
                    if (byteBuffer.remaining() >= 12) {
                        
                        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
                        
                        final int authProtocolNameLength = shortBuffer.get(shortBuffer.position() + 3);
                        final int authProtocolDataLength = shortBuffer.get(shortBuffer.position() + 4);
                        
                        final int authNameLength = authProtocolNameLength + XWindowsProtocolUtil.getPadding(authProtocolNameLength);
                        final int authDataLength = authProtocolDataLength + XWindowsProtocolUtil.getPadding(authProtocolDataLength);
                        
                        final int totalLength = 12 + authNameLength + authDataLength;
                    
                        if (totalLength <= byteBuffer.remaining()) {
                            length = totalLength;
                        }
                        else {
                            length = null;
                        }
                    }
                    else {
                        length = null;
                    }
                }
                else {
                    length = XWindowsProtocolUtil.getMessageLength(byteBuffer);
                }

                return length;
            }

            @Override
            public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                try {
                    XWindowsProtocolServer.this.processMessage(this, byteBuffer, messageLength);
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        };
	}
	
	private static WindowAttributes getRootWindowAttributes(Screen screen) {
	    
	    return new WindowAttributes(
	            WindowAttributes.ALL,
	            PIXMAP.None,
	            new CARD32(0),
	            PIXMAP.None, new CARD32(0),
	            BITGRAVITY.Forget, WINGRAVITY.NorthWest,
	            new BYTE((byte)0),
	            new CARD32(0xFFFFFFFFL), new CARD32(0),
	            new BOOL(false),
	            new BOOL(false),
	            new SETofEVENT(0),
	            new SETofDEVICEEVENT(0),
	            COLORMAP.None,
	            CURSOR.None);
	    
	}
	
	private List<XWindowsScreen> getScreens(GraphicsDriver graphicsDriver) {
	    
	    final List<GraphicsScreen> driverScreens = graphicsDriver.getScreens();
	    final List<XWindowsScreen> screens = new ArrayList<>(driverScreens.size());

	    for (GraphicsScreen driverScreen : driverScreens) {
            
	        final Screen screen = new Screen(driverScreen, new WindowEventListener() {
                @Override
                public void onUpdate(Window window, Region region) {
                    getConnection(window).onUpdate(window, region);
                }
            });
	        
	        final int rootWindow = resourceIdAllocator.allocateRootWindowId();
	        
	        final WINDOW windowResource = new WINDOW(rootWindow);
	        
	        final XWindowsWindow window = new XWindowsWindow(
	                screen.getRootWindow(),
	                WindowClass.InputOnly,
	                getRootWindowAttributes(screen));
	        
	        screens.add(new XWindowsScreen(screen, windowResource, window));
	    }
	    
	    return screens;
	}
	
	
	private abstract class ConnectionState extends XWindowsConnectionState implements Client {

		public ConnectionState(XWindowsProtocolServer server, SocketChannel socketChannel, NonBlockingChannelWriterLog connectionWriteLog) {
			super(
			        server,
			        socketChannel,
			        resourceIdAllocator.allocateConnection(),
			        connectionWriteLog);
		}
	}
	
	private void processMessage(XWindowsConnectionState connectionState, ByteBuffer byteBuffer, int messageLength) throws IOException {

	    switch (connectionState.getState()) {
	    
        case CREATED:
            if (processInitialMessage(connectionState, byteBuffer, messageLength)) {
                
                connectionState.setState(State.INITIAL_RECEIVED);
            }
            else {
                connectionState.setState(State.INITIAL_ERROR);
            }
            break;
            
        case INITIAL_RECEIVED:
            connectionState.setState(State.CONNECTED);

        case CONNECTED:
            processProtocolMessage(connectionState, byteBuffer, messageLength);
            break;
            
        default:
            throw new IllegalStateException();
        }
	}

	
    private boolean processInitialMessage(XWindowsConnectionState connectionState, ByteBuffer byteBuffer, int messageLength) throws IOException {
        
        final ClientMessage clientMessage = ClientMessage.decode(new ByteBufferXWindowsProtocolInputStream(byteBuffer));
        
        final ByteOrder byteOrder;
        
        switch ((char)clientMessage.getByteOrder().getValue()) {
        case 'B':
            byteOrder = ByteOrder.BIG_ENDIAN;
            break;
            
        case 'l':
            byteOrder = ByteOrder.LITTLE_ENDIAN;
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        connectionState.setByteOrder(byteOrder);
        
        // System.out.println("## got client message " + clientMessage);
        
        final ServerMessage serverMessage = constructServerMessage(connectionState.getConnectionNo());
        
        // System.out.println("## sending servermessage " + serverMessage);
        
        /*
        final byte [] encoded = serverMessage.writeToBuf(byteOrder);
        final ByteBuffer buffer = ByteBuffer.wrap(encoded);
        
        buffer.order(byteOrder);
        
        final ServerMessage decoded = ServerMessage.decode(new ByteBufferXWindowsProtocolInputStream(buffer));
        
        System.out.println("Decoded message: " + decoded);
        */
        
        send(connectionState, serverMessage);
        
        return true;
    }
    
    private ServerMessage constructServerMessage(int connectionNo) {
        final String vendor = "Test";
        
        final Set<PixelFormat> distinctPixelFormats = this.screens.stream()
                .map(screen -> screen.getScreen().getDriverScreen().getPixelFormat())
                .collect(Collectors.toSet());
                
        
        final FORMAT [] formats = new FORMAT[distinctPixelFormats.size()];
        
        int dstIdx = 0;
        
        for (PixelFormat pixelFormat : distinctPixelFormats) {
            final FORMAT format = new FORMAT(
                    new CARD8((short)pixelFormat.getDepth()),
                    new CARD8((short)pixelFormat.getBitsPerPixel()),
                    new CARD8((short)0));
            
            formats[dstIdx ++] = format;
        }
        
        final SCREEN [] screens = new SCREEN[this.screens.size()];
        
        for (int i = 0; i < this.screens.size(); ++ i) {
            final XWindowsScreen xWindowsScreen = this.screens.get(i);
            final GraphicsScreen driverScreen = xWindowsScreen.getScreen().getDriverScreen();
            
            final Size size = driverScreen.getSize();
            final Size sizeInMillimeters = driverScreen.getSizeInMillimeters();
            
            final PixelFormat pixelFormat = driverScreen.getPixelFormat();
            
            final VISUALID visualId = new VISUALID(resourceIdAllocator.allocateVisualId());
            
            final VISUALTYPE visual = new VISUALTYPE(
                    visualId,
                    new BYTE((byte)4), // TrueColor
                    new CARD8((short)pixelFormat.getBitsPerColorComponent()),
                    new CARD16(pixelFormat.getNumberOfDistinctColors()),
                    new CARD32(pixelFormat.getRedMask()),
                    new CARD32(pixelFormat.getGreenMask()),
                    new CARD32(pixelFormat.getBlueMask()));
            
            final DEPTH depth = new DEPTH(
                    new CARD8((short)pixelFormat.getDepth()),
                    new CARD16(1),
                    new VISUALTYPE[] { visual });
            
            final SCREEN screen = new SCREEN(
                    xWindowsScreen.getRootWINDOW(),
                    new COLORMAP(0),
                    new CARD32(0x000000), new CARD32(0xFFFFFFF),
                    new SET32(0),
                    new CARD16(size.getWidth()), new CARD16(size.getHeight()),
                    new CARD16(sizeInMillimeters.getWidth()), new CARD16(sizeInMillimeters.getHeight()),
                    new CARD16(0), new CARD16(0),
                    visualId,
                    new BYTE((byte)0), new BOOL((byte)0),
                    new CARD8((short)24), new CARD8((short)1), new DEPTH [] { depth });
         
            screens[i] = screen;
        }
        
        final int vendorAndScreenBytes = 
                vendor.length()
              + XWindowsProtocolUtil.getPadding(vendor.length())
              + length(screens);
        
        final int length = 
                  8 
                + 2 * formats.length
                + (vendorAndScreenBytes / 4);
        
        final ServerMessage serverMessage = new ServerMessage(
                new BYTE((byte)1),
                new CARD16((short)11), new CARD16((short)0),
                new CARD16(length),
                new CARD32(1),
                new CARD32(resourceIdAllocator.getResourceBase(connectionNo)),
                new CARD32(resourceIdAllocator.getResourceMask(connectionNo)),
                new CARD32(0),
                new CARD16(vendor.length()), new CARD16((1 << 15) - 1),
                new CARD8((short)screens.length), new CARD8((short)formats.length),
                new BYTE((byte)0), new BYTE((byte)0), new CARD8((byte)32), new CARD8((byte)32),
                new KEYCODE((short)8), new KEYCODE((short)105),
                vendor,
                formats,
                screens);
        

        return serverMessage;
    }
    
    private static int length(SCREEN [] screens) {
        
        int length = 0;
        
        for (SCREEN screen : screens) {
            length += 40 + length(screen.getAllowedDepths());
        }
        
        return length;
    }
    
    private static int length(DEPTH [] depths) {

        int length = 0;

        for (DEPTH depth : depths) {
            length += 8 + depth.getVisuals().length * 24;
        }
    
        return length;
    }
    
    private <T extends Request> T log(int messageLength, int opcode, CARD16 sequenceNumber, T request) {
        if (protocolLog != null) {
            protocolLog.onReceivedRequest(messageLength, opcode, sequenceNumber, request);
        }

        return request;
    }
    
    private void processProtocolMessage(XWindowsConnectionState connectionState, ByteBuffer byteBuffer, int messageLength) throws IOException {
		
		final int opcode = byteBuffer.get();
		
		final XWindowsProtocolInputStream stream = stream(byteBuffer, messageLength);
		
		final CARD16 sequenceNumber = connectionState.increaseSequenceNumber();
		
		try {
			switch (opcode) {
			case OpCodes.CREATE_WINDOW: {
				final CreateWindow createWindow = log(messageLength, opcode, sequenceNumber, CreateWindow.decode(stream));
	
				final XWindowsWindow window = connectionState.createWindow(display, createWindow);
				
				if (window != null) {
					connectionByWindow.put(window.getWindow(), connectionState);
				}
				break;
			}
			
			case OpCodes.CHANGE_WINDOW_ATTRIBUTES: {
			    log(messageLength, opcode, sequenceNumber, ChangeWindowAttributes.decode(stream));
			    
			    break;
			}
			
			case OpCodes.GET_WINDOW_ATTRIBUTES: {
			    
			    final GetWindowAttributes getWindowAttributes = log(messageLength, opcode, sequenceNumber, GetWindowAttributes.decode(stream));
			    
			    final XWindowsWindow window = connectionState.getWindow(getWindowAttributes.getWindow());

			    if (window == null) {
			        sendError(connectionState, Errors.Window, sequenceNumber, getWindowAttributes.getWindow().getValue(), opcode);
			    }
			    else {
    			    final WindowAttributes curAttributes = window.getCurrentWindowAttributes();
    			    
    			    final GetWindowAttributesReply reply = new GetWindowAttributesReply(
    			            sequenceNumber,
    			            curAttributes.getBackingStore(),
    			            new VISUALID(0),
    			            window.getWindowClass(),
    			            curAttributes.getBitGravity(), curAttributes.getWinGravity(),
    			            curAttributes.getBackingPlanes(), curAttributes.getBackingPixel(),
    			            curAttributes.getSaveUnder(),
    			            new BOOL(true),
    			            MapState.Viewable,
    			            curAttributes.getOverrideRedirect(),
    			            curAttributes.getColormap(),
    			            new SETofEVENT(0), // TODO
    			            new SETofEVENT(0), // TODO
    			            curAttributes.getDoNotPropagateMask());
    			    
    			    sendReply(connectionState, reply);
			    }
			    break;
			}
			
			case OpCodes.DESTROY_WINDOW: {
				final DestroyWindow destroyWindow = log(messageLength, opcode, sequenceNumber, DestroyWindow.decode(stream));
				
				final Window window = connectionState.destroyWindow(display, destroyWindow);
				
				if (window != null) {
					connectionByWindow.remove(window);
				}
				break;
			}
			
			case OpCodes.GET_GEOMETRY:
			    throw new UnsupportedOperationException();
			
			case OpCodes.INTERN_ATOM: {
			    
			    final InternAtom internAtom = log(messageLength, opcode, sequenceNumber, InternAtom.decode(stream));
			    
			    final ATOM atom;
			    
			    if (internAtom.getOnlyIfExists()) {
			        final ATOM existing = atoms.getAtom(internAtom.getName());
			        
			        atom = existing != null ? existing : ATOM.None;
			    }
			    else {
			        atom = atoms.addIfNotExists(internAtom.getName());
			    }
			    
			    sendReply(connectionState, new InternAtomReply(sequenceNumber, atom));
			    break;
			}
			
            case OpCodes.GET_PROPERTY: {
                
                log(messageLength, opcode, sequenceNumber, GetProperty.decode(stream));
                
                sendReply(connectionState, new GetPropertyReply(
                        sequenceNumber,
                        new CARD8((short)0),
                        ATOM.None,
                        new byte[0]));
                break;
            }
            
            case OpCodes.GET_SELECTION_OWNER: {
                
                final GetSelectionOwner getSelectionOwner = log(messageLength, opcode, sequenceNumber, GetSelectionOwner.decode(stream));
                
                sendReply(connectionState, new GetSelectionOwnerReply(sequenceNumber, WINDOW.None));
                break;
            }
            
            case OpCodes.GRAB_SERVER: {
                
                log(messageLength, opcode, sequenceNumber, GrabServer.decode(stream));
                
                break;
            }
			
			case OpCodes.CREATE_PIXMAP: {
			    final CreatePixmap createPixmap = log(messageLength, opcode, sequenceNumber, CreatePixmap.decode(stream));
			    
			    connectionState.createPixmap(createPixmap);
			    break;
			}
			
			case OpCodes.FREE_PIXMAP: {
			    final FreePixmap freePixmap = log(messageLength, opcode, sequenceNumber, FreePixmap.decode(stream));

			    connectionState.freePixmap(freePixmap);
			    break;
			}
			
			case OpCodes.CREATE_GC: {
			    
			    final CreateGC createGC = log(messageLength, opcode, sequenceNumber, CreateGC.decode(stream));
			    
			    connectionState.createGC(createGC);
			    break;
			}
			    
			case OpCodes.PUT_IMAGE: {
			    final PutImage putImage = log(messageLength, opcode, sequenceNumber, PutImage.decode(stream));

			    connectionState.putImage(putImage);
			    break;
			}
			
			case OpCodes.QUERY_EXTENSION: {
			    log(messageLength, opcode, sequenceNumber, QueryExtension.decode(stream));
			
			    sendReply(connectionState, 
			            new QueryResponseReply(
			                    sequenceNumber,
			                    new BOOL((byte)0),
			                    new CARD8((byte)0),
			                    new CARD8((byte)0),
			                    new CARD8((byte)0)));
			    break;
			}
			
			case OpCodes.ALLOC_COLOR: {
			    
			    final AllocColor allocColor = log(messageLength, opcode, sequenceNumber, AllocColor.decode(stream));
			    
			    sendReply(connectionState, new AllocColorReply(
			            sequenceNumber,
			            allocColor.getRed(),
			            allocColor.getGreen(),
			            allocColor.getBlue(),
			            new CARD32(0)));
			    
			    
			    break;
			}
			
			default:
				throw new UnsupportedOperationException("Unknown opcode " + opcode);
			}
		}
		catch (ProtocolException ex) {
			throw new IllegalStateException(ex);
		}
	}

    private void send(XWindowsConnectionState connectionState, Encodeable message) {
        connectionState.send(message);
    }

    private void sendReply(XWindowsConnectionState connectionState, Reply reply) {
        
        if (protocolLog != null) {
            protocolLog.onSendReply(reply);
        }
        
        connectionState.send(reply);
    }

    private void sendError(XWindowsConnectionState connectionState, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
        
        final Error error = new Error(errorCode, sequenceNumber, new CARD32(value), new CARD8((short)opcode));
        
        if (protocolLog != null) {
            protocolLog.onSendError(error);
        }
        
        connectionState.send(error);
    }

    boolean isRootWindow(WINDOW window) {
        return resourceIdAllocator.isRootWindow(window.getValue());
    }
    
    XWindowsWindow getRootWindow(WINDOW windowResource) {

        System.out.println("## getRootWindow " + windowResource);
        
        for (XWindowsScreen screen : screens) {
            
            if (windowResource.equals(screen.getRootWINDOW())) {
                
                return screen.getRootWindow();
                
            }
        }

        return null;
    }
    
	private static XWindowsProtocolInputStream stream(ByteBuffer byteBuffer, int messageLength) {
		return new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	}

	
	private XWindowsConnectionState getConnection(Window window) {
		Objects.requireNonNull(window);

		return connectionByWindow.get(window);
	}

	@Override
    public void close() throws Exception {

	    final Set<XWindowsConnectionState> distinctConnections = new HashSet<>(connectionByWindow.values());
	    
	    for (XWindowsConnectionState connectionState : distinctConnections) {
	        try {
	            connectionState.close();
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
    }
}
