package com.neaterbits.displayserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.events.common.EventSource;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.io.common.Client;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.RevertTo;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.logging.XWindowsProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.replies.AllocColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetGeometryReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetInputFocusReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.messages.replies.InternAtomReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryPointerReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryResponseReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryTreeReply;
import com.neaterbits.displayserver.protocol.messages.requests.AllocColor;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeProperty;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.CreateColorMap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.GetGeometry;
import com.neaterbits.displayserver.protocol.messages.requests.GetInputFocus;
import com.neaterbits.displayserver.protocol.messages.requests.GetProperty;
import com.neaterbits.displayserver.protocol.messages.requests.GetSelectionOwner;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.GrabServer;
import com.neaterbits.displayserver.protocol.messages.requests.InternAtom;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.QueryExtension;
import com.neaterbits.displayserver.protocol.messages.requests.QueryPointer;
import com.neaterbits.displayserver.protocol.messages.requests.QueryTree;
import com.neaterbits.displayserver.protocol.messages.requests.UngrabServer;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XConnection.State;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.Window;

public class XServer implements AutoCloseable {

    private final XWindowsProtocolLog protocolLog;
    private final NonBlockingChannelWriterLog connectionWriteLog;
    
	private final Display display;
	private final ServerResourceIdAllocator resourceIdAllocator;
	private final Atoms atoms;

	private final XState state;
	
	private final long timeServerStarted;
	
	private final ServerToClient serverToClient;
	
	public XServer(
	        EventSource driverEventSource,
	        GraphicsDriver graphicsDriver,
	        XWindowsProtocolLog protocolLog,
	        NonBlockingChannelWriterLog connectionWriteLog) throws IOException {
		
		Objects.requireNonNull(graphicsDriver);
		
		this.protocolLog = protocolLog;
		this.connectionWriteLog = connectionWriteLog;
		
		this.resourceIdAllocator = new ServerResourceIdAllocator();
		
		this.atoms = new Atoms();

		final List<XWindow> rootWindows = new ArrayList<>();
		
		final List<XScreen> screens = ScreensHelper.getScreens(
		        graphicsDriver,
		        new XWindowsEventListener(this),
		        resourceIdAllocator,
		        rootWindows::add);
		
		this.state = new XState(screens);
		
		rootWindows.forEach(state::addRootWindow);
		
		this.display = new Display(screens.stream()
		        .map(screen -> screen.getScreen())
		        .collect(Collectors.toList()));
		
		this.timeServerStarted = System.currentTimeMillis();
		
		this.serverToClient = new ServerToClient() {
            
            @Override
            public void sendReply(XClient client, Reply reply) {
                XServer.this.sendReply(client, reply);
            }
            
            @Override
            public void sendEvent(XClient client, Event event) {
                XServer.this.sendEvent(client, event);
            }
            
            @Override
            public void sendError(XClient client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
                XServer.this.sendError(client, errorCode, sequenceNumber, value, opcode);
            }
        };
	}
	
	XWindowsConstAccess getWindows() {
	    return state;
	}

	XEventSubscriptionsConstAccess getEventSubscriptions() {
	    return state;
	}
	
	TIMESTAMP getTimestamp() {
	    
	    final long diff = System.currentTimeMillis() - timeServerStarted;
	    
	    // + 1 to make sure never returns CurrentTime ( == 0 )
	    return new TIMESTAMP(diff + 1);
	}
	
	public Client processConnection(SocketChannel socketChannel) {
	    return new ConnectionState(XServer.this, socketChannel, connectionWriteLog) {

            @Override
            public Integer getLengthOfMessage(ByteBuffer byteBuffer) {

                final Integer length;
                
                if (getState() == State.CREATED) {
                    
                    if (byteBuffer.get(byteBuffer.position()) == 0x6C) {
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    }

                    length = XWindowsProtocolUtil.getInitialMessageLength(byteBuffer);
                }
                else {
                    length = XWindowsProtocolUtil.getMessageLength(byteBuffer);
                }

                return length;
            }

            @Override
            public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                try {
                    XServer.this.processMessage(this, byteBuffer, messageLength);
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        };
	}
	
	private abstract class ConnectionState extends XClient implements Client {

		public ConnectionState(XServer server, SocketChannel socketChannel, NonBlockingChannelWriterLog connectionWriteLog) {
			super(
			        server,
			        socketChannel,
			        resourceIdAllocator.allocateConnection(),
			        connectionWriteLog);
		}
	}
	
	private void processMessage(XClient client, ByteBuffer byteBuffer, int messageLength) throws IOException {

	    switch (client.getState()) {
	    
        case CREATED:
            if (processInitialMessage(client, byteBuffer, messageLength)) {
                
                client.setState(State.INITIAL_RECEIVED);
            }
            else {
                client.setState(State.INITIAL_ERROR);
            }
            break;
            
        case INITIAL_RECEIVED:
            client.setState(State.CONNECTED);

        case CONNECTED:
            processProtocolMessage(client, byteBuffer, messageLength);
            break;
            
        default:
            throw new IllegalStateException();
        }
	}

	
    private boolean processInitialMessage(XClient client, ByteBuffer byteBuffer, int messageLength) throws IOException {
        
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
        
        client.setByteOrder(byteOrder);
        
        final int connectionNo = client.getConnectionNo();
        
        final ServerMessage serverMessage = InitialServerMessageHelper.constructServerMessage(
                connectionNo,
                state,
                resourceIdAllocator.getResourceBase(connectionNo),
                resourceIdAllocator.getResourceMask(connectionNo),
                resourceIdAllocator::allocateVisualId);

        send(client, serverMessage);
        
        return true;
    }
    
    
    private void processProtocolMessage(XClient client, ByteBuffer byteBuffer, int messageLength) throws IOException {
		
		final int opcode = byteBuffer.get();
		
		final XWindowsProtocolInputStream stream = stream(byteBuffer, messageLength);
		
		final CARD16 sequenceNumber = client.increaseSequenceNumber();
		
		switch (opcode) {
		case OpCodes.CREATE_WINDOW: {
			final CreateWindow createWindow = log(messageLength, opcode, sequenceNumber, CreateWindow.decode(stream));

			final XWindow parentWindow = state.getClientOrRootWindow(createWindow.getParent());
			
			if (parentWindow == null) {
			    sendError(client, Errors.Window, sequenceNumber, createWindow.getParent().getValue(), opcode);
			}
			else {
			
                try {
                    final XWindow window = client.createWindow(display, createWindow, parentWindow);

                    if (window != null) {
                        state.addClientWindow(window, client);
                    }
                } catch (ValueException ex) {
                    sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
                } catch (IDChoiceException ex) {
                    sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
                }
			}
			break;
		}
		
		case OpCodes.CHANGE_WINDOW_ATTRIBUTES: {
		    log(messageLength, opcode, sequenceNumber, ChangeWindowAttributes.decode(stream));
		    
		    break;
		}
		
		case OpCodes.GET_WINDOW_ATTRIBUTES: {
		    
		    final GetWindowAttributes getWindowAttributes = log(messageLength, opcode, sequenceNumber, GetWindowAttributes.decode(stream));

		    MessageProcessorWindows.getWindowAttributes(getWindowAttributes, opcode, sequenceNumber, client, state, serverToClient);
		    break;
		}
		
		case OpCodes.DESTROY_WINDOW: {
			final DestroyWindow destroyWindow = log(messageLength, opcode, sequenceNumber, DestroyWindow.decode(stream));
			
			final XWindow window = client.destroyWindow(display, destroyWindow);
			
			if (window != null) {
			    state.removeClientWindow(window);
			}
			break;
		}
		
		case OpCodes.GET_GEOMETRY: {
		    
		    final GetGeometry getGeometry = log(messageLength, opcode, sequenceNumber, GetGeometry.decode(stream));
		    
		    final WINDOW windowResource = new WINDOW(getGeometry.getDrawable());
		    
            final XWindow window = state.getClientWindow(windowResource);

            if (window == null) {
                sendError(client, Errors.Window, sequenceNumber, windowResource.getValue(), opcode);
            }
            else {
                
                // TODO
                final GetGeometryReply reply = new GetGeometryReply(
                        sequenceNumber,
                        new CARD8(window.getDepth()),
                        window.getRootWINDOW(),
                        new INT16(window.getX()), new INT16(window.getY()),
                        new CARD16(window.getWidth()), new CARD16(window.getHeight()),
                        window.getBorderWidth());
                
                sendReply(client, reply);
            }
		    break;
		}
		
		case OpCodes.QUERY_TREE: {
		    final QueryTree queryTree = log(messageLength, opcode, sequenceNumber, QueryTree.decode(stream));
		    
		    final XWindow xWindow = state.getClientOrRootWindow(queryTree.getWindow());
		    
		    if (xWindow == null) {
		        sendError(client, Errors.Window, sequenceNumber, queryTree.getWindow().getValue(), opcode);
		    }
		    else {

		        final List<Window> children = display.getSubWindowsInOrder(xWindow.getWindow());
		        
		        final QueryTreeReply reply = new QueryTreeReply(
		                sequenceNumber,
		                xWindow.isRootWindow() ? xWindow.getWINDOW() : xWindow.getRootWINDOW(),
		                xWindow.isRootWindow() ? WINDOW.None : xWindow.getParentWINDOW(),
		                children.stream()
		                    .map(w -> state.getClientWindow(w))
		                    .map(xw -> xw.getWINDOW())
		                    .collect(Collectors.toList()));
		
		        sendReply(client, reply);
		    }
		    
		    break;
		}
		
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
		    
		    sendReply(client, new InternAtomReply(sequenceNumber, atom));
		    break;
		}
		
		case OpCodes.CHANGE_PROPERTY: {
		    final ChangeProperty changeProperty = log(messageLength, opcode, sequenceNumber, ChangeProperty.decode(stream));

		    MessageProcessorProperties.changeProperty(changeProperty, opcode, sequenceNumber, getTimestamp(), client, state, serverToClient);
		    break;
		}
		
        case OpCodes.GET_PROPERTY: {
            
            final GetProperty getProperty = log(messageLength, opcode, sequenceNumber, GetProperty.decode(stream));
            
            MessageProcessorProperties.getProperty(getProperty, opcode, sequenceNumber, getTimestamp(), client, state, serverToClient);
            break;
        }
        
        case OpCodes.GET_SELECTION_OWNER: {
            
            final GetSelectionOwner getSelectionOwner = log(messageLength, opcode, sequenceNumber, GetSelectionOwner.decode(stream));
            
            sendReply(client, new GetSelectionOwnerReply(sequenceNumber, WINDOW.None));
            break;
        }
        
        case OpCodes.GRAB_SERVER: {
            
            log(messageLength, opcode, sequenceNumber, GrabServer.decode(stream));
            
            break;
        }

        case OpCodes.UNGRAB_SERVER: {
            
            log(messageLength, opcode, sequenceNumber, UngrabServer.decode(stream));
            
            break;
        }
        
        case OpCodes.QUERY_POINTER: {
            
            final QueryPointer queryPointer = log(messageLength, opcode, sequenceNumber, QueryPointer.decode(stream));
            
            final XWindow window = state.getClientOrRootWindow(queryPointer.getWindow());
            
            if (window == null) {
                sendError(client, Errors.Window, sequenceNumber, queryPointer.getWindow().getValue(), opcode);
            }
            else {
                final QueryPointerReply reply = new QueryPointerReply(
                        sequenceNumber,
                        new BOOL(true),
                        window.isRootWindow() ? window.getWINDOW() : window.getRootWINDOW(),
                        WINDOW.None,
                        new INT16((short)0), new INT16((short)0),
                        new INT16((short)0), new INT16((short)0),
                        new SETofKEYBUTMASK((short)0));
                
                sendReply(client, reply);
            }
            break;
        }

        case OpCodes.GET_INPUT_FOCUS: {
            
            log(messageLength, opcode, sequenceNumber, GetInputFocus.decode(stream));
            
            sendReply(client, new GetInputFocusReply(sequenceNumber, RevertTo.None, WINDOW.None));
            
            break;
        }
        
		case OpCodes.CREATE_PIXMAP: {
		    final CreatePixmap createPixmap = log(messageLength, opcode, sequenceNumber, CreatePixmap.decode(stream));
		    
		    try {
                client.createPixmap(createPixmap);
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.FREE_PIXMAP: {
		    final FreePixmap freePixmap = log(messageLength, opcode, sequenceNumber, FreePixmap.decode(stream));

		    client.freePixmap(freePixmap);
		    break;
		}
		
		case OpCodes.CREATE_GC: {
		    
		    final CreateGC createGC = log(messageLength, opcode, sequenceNumber, CreateGC.decode(stream));
		    
		    client.createGC(createGC);
		    break;
		}
		
		case OpCodes.FREE_GC: {
		    
		    final FreeGC freeGC = log(messageLength, opcode, sequenceNumber, FreeGC.decode(stream));

		    client.freeGC(freeGC);
		    break;
		}
		    
		case OpCodes.PUT_IMAGE: {
		    final PutImage putImage = log(messageLength, opcode, sequenceNumber, PutImage.decode(stream));

		    client.putImage(putImage);
		    break;
		}
		
		case OpCodes.QUERY_EXTENSION: {
		    log(messageLength, opcode, sequenceNumber, QueryExtension.decode(stream));
		
		    sendReply(client, 
		            new QueryResponseReply(
		                    sequenceNumber,
		                    new BOOL((byte)0),
		                    new CARD8((byte)0),
		                    new CARD8((byte)0),
		                    new CARD8((byte)0)));
		    break;
		}
		
		case OpCodes.CREATE_COLOR_MAP: {
		    
		    log(messageLength, opcode, sequenceNumber, CreateColorMap.decode(stream));
		    
		    break;
		}
		
		case OpCodes.ALLOC_COLOR: {
		    
		    final AllocColor allocColor = log(messageLength, opcode, sequenceNumber, AllocColor.decode(stream));
		    
		    sendReply(client, new AllocColorReply(
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

    private void send(XClient client, Encodeable message) {
        client.send(message);
    }

    private void sendEvent(XClient client, Event event) {
        
        if (protocolLog != null) {
            protocolLog.onSendEvent(event);
        }
        
        client.send(event);
    }

    private void sendReply(XClient client, Reply reply) {
        
        if (protocolLog != null) {
            protocolLog.onSendReply(reply);
        }
        
        client.send(reply);
    }

    private void sendError(XClient client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
        
        final Error error = new Error(errorCode, sequenceNumber, new CARD32(value), new CARD8((short)opcode));
        
        if (protocolLog != null) {
            protocolLog.onSendError(error);
        }
        
        client.send(error);
    }

    private <T extends Request> T log(int messageLength, int opcode, CARD16 sequenceNumber, T request) {
        if (protocolLog != null) {
            protocolLog.onReceivedRequest(messageLength, opcode, sequenceNumber, request);
        }

        return request;
    }

	private static XWindowsProtocolInputStream stream(ByteBuffer byteBuffer, int messageLength) {
		return new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	}

	@Override
    public void close() throws Exception {

	    for (XClient client : state.getClients()) {
	        try {
	            client.close();
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
    }
}
