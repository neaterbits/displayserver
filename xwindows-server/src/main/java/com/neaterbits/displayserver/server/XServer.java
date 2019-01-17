package com.neaterbits.displayserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.io.common.Client;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.server.XConnection.State;
import com.neaterbits.displayserver.windows.Display;
import com.neaterbits.displayserver.windows.DisplayAreas;
import com.neaterbits.displayserver.windows.WindowsDisplayAreas;
import com.neaterbits.displayserver.xwindows.core.processing.XCoreModule;
import com.neaterbits.displayserver.xwindows.model.XScreensAndVisuals;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.processing.XMessageDispatcher;

public class XServer implements AutoCloseable {

    private final XRendering rendering;
    
    private final NonBlockingChannelWriterLog connectionWriteLog;
    
	private final Display display;
	private final ServerResourceIdAllocator resourceIdAllocator;

	private final XState state;
	
	private final long timeServerStarted;
	
	private final XMessageDispatcher messageDispatcher;
	
	public XServer(
	        XHardware hardware,
	        XConfig config,
	        XRendering rendering,
	        XWindowsServerProtocolLog protocolLog,
	        NonBlockingChannelWriterLog connectionWriteLog) throws IOException {
		
		Objects.requireNonNull(hardware);
		Objects.requireNonNull(rendering);
		
		this.rendering = rendering;
		
		this.connectionWriteLog = connectionWriteLog;
		
		this.resourceIdAllocator = new ServerResourceIdAllocator();
		
		final Map<XWindow, Integer> rootWindows = new HashMap<>();

		final XWindowsEventListener eventListener = new XWindowsEventListener(this);
		
		final DisplayAreas displayAreas = rendering.getDisplayAreas();
		
		final WindowsDisplayAreas windowsDisplayAreas = displayAreas.toWindowsDisplayAreas(eventListener);

		final XScreensAndVisuals screens = ScreensHelper.getScreens(
		        hardware.getGraphicsDriver(),
		        windowsDisplayAreas,
		        resourceIdAllocator,
		        rendering,
		        (screenNo, window) -> rootWindows.put(window, screenNo));
		
		this.state = new XState(screens);
		
		rootWindows.entrySet().forEach(entry -> state.addRootWindow(entry.getValue(), entry.getKey()));
		
		this.display = new Display(windowsDisplayAreas);
		
		this.timeServerStarted = System.currentTimeMillis();
		
        final XTimestampGenerator timestampGenerator = new XTimestampGenerator() {
            @Override
            public TIMESTAMP getTimestamp() {
                return XServer.this.getTimestamp();
            }
        };
        
        this.messageDispatcher = new XCoreModule(
                protocolLog,
                display,
                state.getScreens(),
                state.getVisuals(),
                state.getWindows(),
                state.getPixmaps(),
                rendering.getCompositor(),
                rendering.getRendererFactory(),
                rendering.getFontBufferFactory(),
                timestampGenerator,
                hardware.getInputDriver(),
                config);
	}
	
	XClientWindowsConstAccess getWindows() {
	    return state;
	}

	XEventSubscriptionsConstAccess getEventSubscriptions() {
	    return state;
	}
	
	private TIMESTAMP getTimestamp() {
	    
	    final long diff = System.currentTimeMillis() - timeServerStarted;
	    
	    // + 1 to make sure never returns CurrentTime ( == 0 )
	    return new TIMESTAMP(diff + 1);
	}
	
	public Client processConnection(SocketChannel socketChannel, SelectionKey selectionKey) {
	    return new ConnectionState(socketChannel, selectionKey, connectionWriteLog) {

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
		        SocketChannel socketChannel,
		        SelectionKey selectionKey,
		        NonBlockingChannelWriterLog connectionWriteLog) {
			super(
			        socketChannel,
			        selectionKey,
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
            final int opcode = byteBuffer.get();
            
            final XWindowsProtocolInputStream stream = stream(byteBuffer, messageLength);
            
            final CARD16 sequenceNumber = client.increaseSequenceNumber();

            messageDispatcher.processMessage(stream, messageLength, opcode, sequenceNumber, client);
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
    
    
    private void send(XClient client, Encodeable message) {
        client.send(message);
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
