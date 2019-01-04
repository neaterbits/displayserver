package com.neaterbits.displayserver.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.events.common.KeyboardMapping;
import com.neaterbits.displayserver.events.common.Modifier;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.io.common.Client;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.RevertTo;
import com.neaterbits.displayserver.protocol.enums.VisualClass;
import com.neaterbits.displayserver.protocol.exception.ColormapException;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
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
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.messages.replies.InternAtomReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryPointerReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryResponseReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryTreeReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.LookupColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.QueryColorsReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.RGB;
import com.neaterbits.displayserver.protocol.messages.requests.AllocColor;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeGC;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeProperty;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.ClearArea;
import com.neaterbits.displayserver.protocol.messages.requests.ConfigureWindow;
import com.neaterbits.displayserver.protocol.messages.requests.ConvertSelection;
import com.neaterbits.displayserver.protocol.messages.requests.CopyArea;
import com.neaterbits.displayserver.protocol.messages.requests.CreateColorMap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateCursor;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateWindow;
import com.neaterbits.displayserver.protocol.messages.requests.DeleteProperty;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.FreeColors;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.GetGeometry;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.GetInputFocus;
import com.neaterbits.displayserver.protocol.messages.requests.GetKeyboardMapping;
import com.neaterbits.displayserver.protocol.messages.requests.GetModifierMapping;
import com.neaterbits.displayserver.protocol.messages.requests.GetProperty;
import com.neaterbits.displayserver.protocol.messages.requests.GetSelectionOwner;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.GrabButton;
import com.neaterbits.displayserver.protocol.messages.requests.GrabServer;
import com.neaterbits.displayserver.protocol.messages.requests.InternAtom;
import com.neaterbits.displayserver.protocol.messages.requests.ListProperties;
import com.neaterbits.displayserver.protocol.messages.requests.MapSubwindows;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.QueryExtension;
import com.neaterbits.displayserver.protocol.messages.requests.QueryPointer;
import com.neaterbits.displayserver.protocol.messages.requests.QueryTree;
import com.neaterbits.displayserver.protocol.messages.requests.RecolorCursor;
import com.neaterbits.displayserver.protocol.messages.requests.SetCloseDownMode;
import com.neaterbits.displayserver.protocol.messages.requests.SetInputFocus;
import com.neaterbits.displayserver.protocol.messages.requests.UngrabServer;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CloseFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CreateGlyphCursor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.FreeCursor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.ImageText16;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.LookupColor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.OpenFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyFillRectangle;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyLine;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyPoint;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolySegment;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryColors;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.KEYSYM;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XConnection.State;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.DisplayAreaFinder;
import com.neaterbits.displayserver.windows.DisplayAreaWindows;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.model.Atoms;
import com.neaterbits.displayserver.xwindows.model.XBuiltinColor;
import com.neaterbits.displayserver.xwindows.model.XBuiltinColors;
import com.neaterbits.displayserver.xwindows.model.XColorMap;
import com.neaterbits.displayserver.xwindows.model.XColorMaps;
import com.neaterbits.displayserver.xwindows.model.XDrawablesConstAccess;
import com.neaterbits.displayserver.xwindows.model.XPixmap;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreensAndVisuals;
import com.neaterbits.displayserver.xwindows.model.XVisual;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.util.Unsigned;

public class XServer implements AutoCloseable {

    private final XHardware hardware;

    private final XRendering rendering;
    
    private final XWindowsServerProtocolLog protocolLog;
    private final NonBlockingChannelWriterLog connectionWriteLog;
    
	private final Display display;
	private final ServerResourceIdAllocator resourceIdAllocator;
	private final Atoms atoms;

	private final XState state;
	
	private final XFonts fonts;
	
	private final XColorMaps colormaps;
	
	private final long timeServerStarted;
	
	private final ServerToClient serverToClient;
	
	private final XBuiltinColors builtinColors;
	
	public XServer(
	        XHardware hardware,
	        XConfig config,
	        XRendering rendering,
	        XWindowsServerProtocolLog protocolLog,
	        NonBlockingChannelWriterLog connectionWriteLog) throws IOException {
		
		Objects.requireNonNull(hardware);
		Objects.requireNonNull(rendering);
		
		this.hardware = hardware;
		this.rendering = rendering;
		
		this.protocolLog = protocolLog;
		this.connectionWriteLog = connectionWriteLog;
		
		this.resourceIdAllocator = new ServerResourceIdAllocator();
		
		this.atoms = new Atoms();

		this.fonts = new XFonts(config.getFontPaths(), atoms::addIfNotExists);
		
		this.colormaps = new XColorMaps();
		
		final Map<XWindow, Integer> rootWindows = new HashMap<>();

		final XWindowsEventListener eventListener = new XWindowsEventListener(this);
		
		final DisplayAreaWindows displayArea = DisplayAreaFinder.makeDisplayArea(
		        config.getDisplayAreaConfig(),
		        hardware.getGraphicsDriver(),
		        eventListener);
		
		if (displayArea == null) {
		    throw new IllegalStateException();
		}
		
		final List<DisplayAreaWindows> displayAreas = Arrays.asList(
                displayArea
        );

		final XScreensAndVisuals screens = ScreensHelper.getScreens(
		        hardware.getGraphicsDriver(),
		        displayAreas,
		        resourceIdAllocator,
		        rendering,
		        (screenNo, window) -> rootWindows.put(window, screenNo));
		
		this.state = new XState(screens);
		
		rootWindows.entrySet().forEach(entry -> state.addRootWindow(entry.getValue(), entry.getKey()));
		
		this.display = new Display(displayAreas);
		
		this.timeServerStarted = System.currentTimeMillis();
		
		this.serverToClient = new ServerToClient() {
            
            @Override
            public void sendReply(XClient client, Reply reply) {
                XServer.this.sendReply(client, reply);
            }
            
            @Override
            public void sendEvent(XClient client, WINDOW window, Event event) {
                XServer.this.sendEvent(client, window, event);
            }
            
            @Override
            public void sendError(XClient client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
                XServer.this.sendError(client, errorCode, sequenceNumber, value, opcode);
            }
        };
        
        final File colorsFile = new File(config.getColorsFile());
        
        try (FileInputStream colorsInputStream = new FileInputStream(colorsFile)) {
            this.builtinColors = XBuiltinColors.decode(colorsInputStream);
        }
	}
	
	XClientWindowsConstAccess getWindows() {
	    return state;
	}
	
	XPixmapsConstAccess getPixmaps() {
	    return state;
	}
	
	XDrawablesConstAccess getDrawables() {
	    return state;
	}

	XEventSubscriptionsConstAccess getEventSubscriptions() {
	    return state;
	}
	
	DisplayAreaWindows findDisplayArea(DRAWABLE drawable) {
	    return state.findDisplayArea(drawable);
	}
	
	TIMESTAMP getTimestamp() {
	    
	    final long diff = System.currentTimeMillis() - timeServerStarted;
	    
	    // + 1 to make sure never returns CurrentTime ( == 0 )
	    return new TIMESTAMP(diff + 1);
	}
	
	public Client processConnection(SocketChannel socketChannel) {
	    return new ConnectionState(XServer.this, socketChannel, connectionWriteLog, rendering) {

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
                    length = XWindowsProtocolUtil.getRequestLength(byteBuffer);
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

		public ConnectionState(
		        XServer server,
		        SocketChannel socketChannel,
		        NonBlockingChannelWriterLog connectionWriteLog,
		        XRendering rendering) {
			super(
			        server,
			        socketChannel,
			        resourceIdAllocator.allocateConnection(),
			        connectionWriteLog,
			        rendering);
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
                state,
                rendering.getRendererFactory(),
                resourceIdAllocator.getResourceBase(connectionNo),
                resourceIdAllocator.getResourceMask(connectionNo));

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
		
		case OpCodes.MAP_WINDOW: {
		    
		    log(messageLength, opcode, sequenceNumber, MapWindow.decode(stream));
		    
		    break;
		}
		
		case OpCodes.MAP_SUBWINDOWS: {
		    
		    log(messageLength, opcode, sequenceNumber, MapSubwindows.decode(stream));

		    break;
		}
		
		case OpCodes.CONFIGURE_WINDOW: {
		    
		    log(messageLength, opcode, sequenceNumber, ConfigureWindow.decode(stream));
		    
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
		
		case OpCodes.DELETE_PROPERTY: {
		
		    final DeleteProperty deleteProperty = log(messageLength, opcode, sequenceNumber, DeleteProperty.decode(stream));
		    
		    MessageProcessorProperties.deleteProperty(deleteProperty, opcode, sequenceNumber, getTimestamp(), client, state, serverToClient);
		    break;
		}
		
        case OpCodes.GET_PROPERTY: {
            
            final GetProperty getProperty = log(messageLength, opcode, sequenceNumber, GetProperty.decode(stream));
            
            MessageProcessorProperties.getProperty(getProperty, opcode, sequenceNumber, getTimestamp(), client, state, serverToClient);
            break;
        }
        
        case OpCodes.LIST_PROPERTIES: {

            final ListProperties listProperties = log(messageLength, opcode, sequenceNumber, ListProperties.decode(stream));
            
            MessageProcessorProperties.listProperties(listProperties, opcode, sequenceNumber, client, state, serverToClient);
            break;
        }
        
        case OpCodes.GET_SELECTION_OWNER: {
            
            final GetSelectionOwner getSelectionOwner = log(messageLength, opcode, sequenceNumber, GetSelectionOwner.decode(stream));
            
            sendReply(client, new GetSelectionOwnerReply(sequenceNumber, WINDOW.None));
            break;
        }
        
        case OpCodes.CONVERT_SELECTION: {
            
            log(messageLength, opcode, sequenceNumber, ConvertSelection.decode(stream));
            
            break;
        }
        
        case OpCodes.GRAB_BUTTON: {
            
            log(messageLength, opcode, sequenceNumber, GrabButton.decode(stream));
            
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

        case OpCodes.SET_INPUT_FOCUS: {
            
            log(messageLength, opcode, sequenceNumber, SetInputFocus.decode(stream));
            
            break;
        }
        
        case OpCodes.GET_INPUT_FOCUS: {
            
            log(messageLength, opcode, sequenceNumber, GetInputFocus.decode(stream));
            
            sendReply(client, new GetInputFocusReply(sequenceNumber, RevertTo.None, WINDOW.None));
            
            break;
        }
        
        case OpCodes.OPEN_FONT: {
            final OpenFont openFont = log(messageLength, opcode, sequenceNumber, OpenFont.decode(stream));
            
            try {
                
                final String fontName = openFont.getName().equals("fixed")
                        ? "7x13"
                        : openFont.getName();
                
                final XFont font = fonts.openFont(fontName, rendering.getFontBufferFactory());
                
                client.openFont(openFont, font);
                
            } catch (NoSuchFontException ex) {
                sendError(client, Errors.Name, sequenceNumber, openFont.getFid().getValue(), opcode);
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.CLOSE_FONT: {
            
            final CloseFont closeFont = log(messageLength, opcode, sequenceNumber, CloseFont.decode(stream));
            
            try {
                final XFont font = client.closeFont(closeFont);

                fonts.closeFont(font);
            } catch (FontException ex) {
                sendError(client, Errors.Font, sequenceNumber, ex.getFont().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.QUERY_FONT: {
            final QueryFont queryFont = log(messageLength, opcode, sequenceNumber, QueryFont.decode(stream));
            
            try {
                client.queryFont(queryFont, sequenceNumber, serverToClient);
            } catch (FontException ex) {
                sendError(client, Errors.Font, sequenceNumber, ex.getFont().getValue(), opcode);
            }
            break;
        }

        case OpCodes.CREATE_PIXMAP: {
		    final CreatePixmap createPixmap = log(messageLength, opcode, sequenceNumber, CreatePixmap.decode(stream));
		    
		    try {
                final XPixmap xPixmap = client.createPixmap(createPixmap);
                
                state.addPixmap(createPixmap.getPid(), createPixmap.getDrawable(), xPixmap);
                
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.FREE_PIXMAP: {
		    final FreePixmap freePixmap = log(messageLength, opcode, sequenceNumber, FreePixmap.decode(stream));

	        final DRAWABLE pixmapDrawable = freePixmap.getPixmap().toDrawable();
	        
	        final DisplayAreaWindows displayArea = findDisplayArea(pixmapDrawable);
		    
		    final XPixmap xPixmap = state.removePixmap(freePixmap.getPixmap());
		    
		    if (xPixmap != null) {
		        client.freePixmap(freePixmap, xPixmap, displayArea);
		    }
		    break;
		}
		
		case OpCodes.CREATE_GC: {
		    
		    final CreateGC createGC = log(messageLength, opcode, sequenceNumber, CreateGC.decode(stream));
		    
		    try {
                client.createGC(createGC);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
		    catch (IDChoiceException ex) {
		        sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
		    }
		    break;
		}
		
		case OpCodes.CHANGE_GC: {
		    
		    final ChangeGC changeGC = log(messageLength, opcode, sequenceNumber, ChangeGC.decode(stream));
		    
		    try {
                client.changeGC(changeGC);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, changeGC.getGc().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.FREE_GC: {
		    
		    final FreeGC freeGC = log(messageLength, opcode, sequenceNumber, FreeGC.decode(stream));

		    try {
                client.freeGC(freeGC);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.CLEAR_AREA: {
		    
		    final ClearArea clearArea = log(messageLength, opcode, sequenceNumber, ClearArea.decode(stream));
		    
		    client.clearArea(clearArea);
		    
		    break;
		}
		
		case OpCodes.COPY_AREA: {
		    
		    final CopyArea copyArea = log(messageLength, opcode, sequenceNumber, CopyArea.decode(stream));
		    
		    try {
                client.copyArea(copyArea);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.POLY_POINT: {
		    
		    final PolyPoint polyPoint = log(messageLength, opcode, sequenceNumber, PolyPoint.decode(stream));
		    
            try {
                client.polyPoint(polyPoint);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.POLY_LINE: {
		    
		    final PolyLine polyLine = log(messageLength, opcode, sequenceNumber, PolyLine.decode(stream));
		    
		    try {
                client.polyLine(polyLine);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.POLY_SEGMENT: {
		    
		    log(messageLength, opcode, sequenceNumber, PolySegment.decode(stream));
		    
		    break;
		}
		
		
		case OpCodes.POLY_FILL_RECTANGLE: {
		
		    final PolyFillRectangle polyFillRectangle = log(messageLength, opcode, sequenceNumber, PolyFillRectangle.decode(stream));
		    
		    
		    break;
		}
		    
		case OpCodes.PUT_IMAGE: {
		    final PutImage putImage = log(messageLength, opcode, sequenceNumber, PutImage.decode(stream));

		    try {
                client.putImage(putImage);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
		    break;
		}
		
        case OpCodes.GET_IMAGE: {
            final GetImage getImage = log(messageLength, opcode, sequenceNumber, GetImage.decode(stream));

            try {
                client.getImage(getImage, sequenceNumber, serverToClient);
            } catch (MatchException ex) {
                sendError(client, Errors.Match, sequenceNumber, 0L, opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.IMAGE_TEXT_16: {

            final ImageText16 imageText16 = log(messageLength, opcode, sequenceNumber, ImageText16.decode(stream));
            
            try {
                client.imageText16(imageText16);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            } catch (MatchException ex) {
                sendError(client, Errors.Match, sequenceNumber, 0L, opcode);
            }
            break;
        }

        case OpCodes.CREATE_COLOR_MAP: {
		    
		    final CreateColorMap createColorMap = log(messageLength, opcode, sequenceNumber, CreateColorMap.decode(stream));
		    
		    final XWindow xWindow = getWindows().getClientOrRootWindow(createColorMap.getWindow());
		    
		    if (xWindow == null) {
		        sendError(client, Errors.Window, sequenceNumber, createColorMap.getWindow().getValue(), opcode);
		    }
		    else if (colormaps.contains(createColorMap.getMid())) {
		        sendError(client, Errors.IDChoice, sequenceNumber, createColorMap.getMid().getValue(), opcode);
		    }
		    else {
		        final Integer screenNo = state.getScreenForWindow(xWindow.getWINDOW());
		        
		        if (screenNo == null) {
	                sendError(client, Errors.Window, sequenceNumber, createColorMap.getWindow().getValue(), opcode);
		        }
		        else {
		            final XScreen xScreen = state.getScreen(screenNo);
		            
		            if (xScreen == null) {
		                throw new IllegalStateException();
		            }

		            final XVisual xVisual = state.getVisual(createColorMap.getVisual());
		            
		            if (xVisual == null || !xScreen.supportsVisual(xVisual, state)) {
		                sendError(client, Errors.Match, sequenceNumber, createColorMap.getVisual().getValue(), opcode);
		            }
		            else {
		                final XColorMap colormap = new XColorMap(xScreen, xVisual);
		                
		                colormaps.add(createColorMap.getMid(), colormap);
		            }
		        }
		    }
		    break;
		}
		
		case OpCodes.ALLOC_COLOR: {
		    
		    final AllocColor allocColor = log(messageLength, opcode, sequenceNumber, AllocColor.decode(stream));

		    try {
                final PixelFormat pixelFormat = getPixelFormat(allocColor.getCmap());
    
                final int pixel = getPixel(
                        pixelFormat,
                        allocColor.getRed(),
                        allocColor.getGreen(),
                        allocColor.getBlue());
                
    		    sendReply(client, new AllocColorReply(
    		            sequenceNumber,
    		            getRed(pixelFormat, pixel),
    		            getGreen(pixelFormat, pixel),
    		            getBlue(pixelFormat, pixel),
    		            new CARD32(Unsigned.intToUnsigned(pixel))));
		    }
		    catch (ColormapException ex) {
		        sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
		    }
		    break;
		}
		
		case OpCodes.FREE_COLORS: {
		    
		    log(messageLength, opcode, sequenceNumber, FreeColors.decode(stream));
		    
		    break;
		}
		
        case OpCodes.QUERY_COLORS: {
            
            final QueryColors queryColors = log(messageLength, opcode, sequenceNumber, QueryColors.decode(stream));

            try {
                final PixelFormat pixelFormat = getPixelFormat(queryColors.getCmap());
                
                final CARD32 [] pixels = queryColors.getPixels();
                
                final RGB [] colors = new RGB[pixels.length];
                
                for (int i = 0; i < pixels.length; ++ i) {
    
                    final int pixel = (int)pixels[i].getValue();
                    
                    colors[i] = new RGB(
                            getRed(pixelFormat, pixel),
                            getGreen(pixelFormat, pixel),
                            getBlue(pixelFormat, pixel));
                }
                
                sendReply(client, new QueryColorsReply(sequenceNumber, colors));
            }
            catch (ColormapException ex) {
                sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
            }
            break;
        }
		
		case OpCodes.LOOKUP_COLOR: {
		    
		    final LookupColor lookupColor = log(messageLength, opcode, sequenceNumber, LookupColor.decode(stream));

		    final XBuiltinColor builtinColor = builtinColors.getColor(lookupColor.getName());
		    
		    if (builtinColor == null) {
		        sendError(client, Errors.Name, sequenceNumber, 0L, opcode);
		    }
		    else if (!lookupColor.getCmap().equals(COLORMAP.None)) {
		        throw new UnsupportedOperationException("TODO");
		    }
		    else {
		        sendReply(client, new LookupColorReply(
		                sequenceNumber,
		                
		                new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256),
		                
                        new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256)));
		    }
		    break;
		}
		
		case OpCodes.CREATE_CURSOR: {
		    
		    final CreateCursor createCursor = log(messageLength, opcode, sequenceNumber, CreateCursor.decode(stream));
		    
		    try {
                client.createCursor(createCursor);
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, createCursor.getCID().getValue(), opcode);
            }
		    break;
		}
		
		case OpCodes.CREATE_GLYPH_CURSOR: {
		    
		    log(messageLength, opcode, sequenceNumber, CreateGlyphCursor.decode(stream));
		    
		    break;
		}
		
		case OpCodes.FREE_CURSOR: {
		    
		    log(messageLength, opcode, sequenceNumber, FreeCursor.deccode(stream));
		    
		    break;
		}
		
		case OpCodes.RECOLOR_CURSOR: {
		    log(messageLength, opcode, sequenceNumber, RecolorCursor.decode(stream));
		
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
        
        case OpCodes.GET_KEYBOARD_MAPPING: {
            
            final GetKeyboardMapping getKeyboardMapping = log(messageLength, opcode, sequenceNumber, GetKeyboardMapping.decode(stream));
            
            final KeyboardMapping keyboardMapping = hardware.getInputDriver().getKeyboardMapping();
         
            final int index = getKeyboardMapping.getFirstKeycode().getValue() - keyboardMapping.getMinScancode();
            
            final int count = getKeyboardMapping.getCount().getValue();
            
            if (index < 0) {
                sendError(client, Errors.Value, sequenceNumber, getKeyboardMapping.getFirstKeycode().getValue(), opcode);
            }
            else if (index + getKeyboardMapping.getCount().getValue() > keyboardMapping.getNumScancodes()) {
                sendError(client, Errors.Value, sequenceNumber, count, opcode);
            }
            else {
                
                final int keysymsPerKeycode = Arrays.stream(keyboardMapping.getScancodeToKeysym())
                        .skip(index)
                        .limit(count)
                        .map(keysyms -> keysyms.length)
                        .max(Integer::compare)
                        .orElse(0);
                
                
                final KEYSYM [] keysyms = new KEYSYM[count * keysymsPerKeycode];
                
                int dstIdx = 0;
                
                for (int i = 0; i < count; ++ i) {
                    final int [] srcKeysyms = keyboardMapping.getKeysyms(index + i);
                    
                    if (srcKeysyms.length > keysymsPerKeycode) {
                        throw new IllegalStateException();
                    }
                    
                    for (int j = 0; j < srcKeysyms.length; ++ j) {
                        keysyms[dstIdx ++] = new KEYSYM(srcKeysyms[j]);
                    }
                    
                    final int remaining = keysymsPerKeycode - srcKeysyms.length;

                    for (int j = 0; j < remaining; ++ j) {
                        keysyms[dstIdx ++] = KEYSYM.NoSymbol;
                    }
                }
                
                if (dstIdx != keysyms.length) {
                    throw new IllegalStateException();
                }
                
                final GetKeyboardMappingReply reply = new GetKeyboardMappingReply(
                        sequenceNumber,
                        new BYTE((byte)keysymsPerKeycode),
                        keysyms);
                
                sendReply(client, reply);
            }
            break;
        }
        
        case OpCodes.SET_CLOSE_DOWN_MODE: {
            
            log(messageLength, opcode, sequenceNumber, SetCloseDownMode.decode(stream));
            
            break;
        }
        
        case OpCodes.GET_MODIFIER_MAPPING: {
            
            log(messageLength, opcode, sequenceNumber, GetModifierMapping.decode(stream));
            
            final ModifierScancodes modifierScancodes = hardware.getInputDriver().getModifierScancodes();
            
            final CARD8 [] keycodes = new CARD8[modifierScancodes.getCodesPerModifier() * 8];
            
            int dstIdx = 0;
            
            for (Modifier modifier : modifierScancodes.getModifiers()) {
                for (short scancode : modifier.getScancodes()) {
                    keycodes[dstIdx ++] = new CARD8(scancode);
                }
            }
            
            sendReply(client, new GetModifierMappingReply(
                    sequenceNumber,
                    new BYTE((byte)modifierScancodes.getCodesPerModifier()),
                    keycodes));
            break;
        }
        
		default:
			throw new UnsupportedOperationException("Unknown opcode " + opcode);
		}
	}
    
    private PixelFormat getPixelFormat(COLORMAP cmap) throws ColormapException {
        
        final PixelFormat pixelFormat;
        
        if (cmap.equals(COLORMAP.None)) {
            pixelFormat = PixelFormat.RGB32;
        }
        else {
            final XColorMap xColorMap = colormaps.get(cmap);
            
            if (xColorMap == null) {
                throw new ColormapException("No such colormap", cmap);
            }
            
            switch (xColorMap.getVisualClass()) {
            case VisualClass.TRUECOLOR:
                pixelFormat = PixelFormat.RGB32;
                break;
                
            default:
                throw new UnsupportedOperationException("TODO");
            }
        }

        return pixelFormat;
    }
    
    private static CARD16 getRed(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getRed(pixel) * 256);
    }

    private static CARD16 getGreen(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getGreen(pixel) * 256);
    }

    private static CARD16 getBlue(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getBlue(pixel) * 256);
    }
    
    private static int getPixel(PixelFormat pixelFormat, CARD16 red, CARD16 green, CARD16 blue) {
        return pixelFormat.getPixel(red.getValue() / 256, green.getValue() / 256, blue.getValue() / 256);
    }

    private void send(XClient client, Encodeable message) {
        client.send(message);
    }

    private void sendEvent(XClient client, WINDOW window, Event event) {
        
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
