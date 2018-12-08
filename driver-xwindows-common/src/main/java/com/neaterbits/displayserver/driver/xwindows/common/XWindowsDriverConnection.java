package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.neaterbits.displayserver.driver.common.Listeners;
import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientConnectionError;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class XWindowsDriverConnection
		implements AutoCloseable, XWindowsRequestSender {

    private final XWindowsClientProtocolLog protocolLog;
	private final XWindowsChannelReaderWriter readerWriter;
	
	private final Listeners<XWindowsReplyListener> replyListeners;
	private final Listeners<XWindowsEventListener> eventListeners;
	
	private ServerMessage serverMessage;
	private ClientResourceIdAllocator clientResourceIdAllocator;
	
	private final ByteOrder byteOrder;
	
	public XWindowsDriverConnection(int connectDisplay, NonBlockingChannelWriterLog writeLog, XWindowsClientProtocolLog protocolLog) throws IOException {

	    this.protocolLog = protocolLog;
	    
	    final int port = 6000 + connectDisplay;
	    
		this.readerWriter = new XWindowsChannelReaderWriter(port, writeLog) {
		    
		    @Override
            protected boolean receivedInitialMessage() {
                return serverMessage != null;
            }

            @Override
		    public void onMessage(ByteBuffer byteBuffer, int messageLength) {
		        
		        if (byteBuffer.get(byteBuffer.position()) == 0) {
		            
		            if (receivedInitialMessage()) {
		                try {
                            readError(byteBuffer);
                        } catch (IOException ex) {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }
		            }
		            else {
		                readInitialMessageError(byteBuffer);
		            }
		        }
		        else {
		        
    		        try {
        		        if (serverMessage == null) {
        		            serverMessage = processServerMessage(byteBuffer, messageLength);
        		            
        		            // System.out.println("## received servermessage " + serverMessage);
        		            
        		            clientResourceIdAllocator = new ClientResourceIdAllocator(
        		                    serverMessage.getResourceIdBase().getValue(),
        		                    serverMessage.getResourceIdMask().getValue());
        		        }
        		        else {
        		        
        		            processMessage(byteBuffer, messageLength);
        		        }
    		        }
    		        catch (IOException ex) {
    		            throw new IllegalStateException(ex);
    		        }
		        }
		    }
		};
		
		this.replyListeners = new Listeners<>();
		this.eventListeners = new Listeners<>();
		
		this.byteOrder = ByteOrder.BIG_ENDIAN;
		
		final List<XAuth> xAuths = XAuth.getXAuthInfo();
		
		final XAuth xAuthForTCPConnection = xAuths.stream()
		        .filter(xAuth -> xAuth.getAuthorizationProtocol().equals("MIT-MAGIC-COOKIE-1"))
		        .filter(xAuth -> xAuth.getCommunicationProtocol() == null)
		        .filter(xAuth -> xAuth.getDisplay() == connectDisplay)
		        .findFirst()
		        .orElse(null);
		
		if (xAuthForTCPConnection == null) {
		    throw new IllegalStateException();
		}
		
		final ClientMessage clientMessage = new ClientMessage(
		        new CARD8((short)'B'),
		        new CARD16(11),
		        new CARD16(0),
		        xAuthForTCPConnection.getAuthorizationProtocol(),
		        xAuthForTCPConnection.getAuthorizationData());
		
		readerWriter.writeEncodeable(clientMessage, byteOrder);
	}
	
	private void readInitialMessageError(ByteBuffer byteBuffer) {

	    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	    
	    try {
	        
	        final byte errorCode = byteBuffer.get(byteBuffer.position() + 1);
	        final int sequenceNumber = byteBuffer.getShort(byteBuffer.position() + 3);
	        
            final ClientConnectionError clientError = ClientConnectionError.decode(stream);

            final String reason = clientError.getReason();
            
            if (protocolLog != null) {
                protocolLog.onInitialMessageError(errorCode, sequenceNumber, reason);
            }
	    }
	    catch (IOException ex) {
	        throw new IllegalStateException(ex);
	    }
	}
	
	private void readError(ByteBuffer byteBuffer) throws IOException {

	    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	    
	    final com.neaterbits.displayserver.protocol.messages.Error error = com.neaterbits.displayserver.protocol.messages.Error.decode(stream);

	    if (protocolLog != null) {
	        protocolLog.onReceivedError(error);
	    }
	}
	
	
	private static ServerMessage processServerMessage(ByteBuffer byteBuffer, int messageLength) throws IOException {

	    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	    
	    return ServerMessage.decode(stream);
	}

	private void processMessage(ByteBuffer byteBuffer, int messageLength) {
        final int opcode = byteBuffer.get();
        
        switch (opcode) {
        
        default:
            throw new UnsupportedOperationException("Unknown opcode " + opcode);
        }
	}
	
	public ServerMessage getServerMessage() {
        return serverMessage;
    }
	
	public int allocateResourceId() {
	    return clientResourceIdAllocator.allocateResourceId();
	}

	public void freeResourceId(int resourceId) {
	    clientResourceIdAllocator.freeResourceId(resourceId);
	}
	
    public void setServerMessage(ServerMessage serverMessage) {
        this.serverMessage = serverMessage;
    }

    public void registerReplyListener(XWindowsReplyListener replyListener) {
	    replyListeners.registerListener(replyListener);
	}
	
    public void deregisterReplyListener(XWindowsReplyListener replyListener) {
        replyListeners.deregisterListener(replyListener);
    }
	
    public void registerEventListener(XWindowsEventListener eventListener) {
        eventListeners.registerListener(eventListener);
    }

    public void deregisterEventListener(XWindowsEventListener eventListener) {
        eventListeners.deregisterListener(eventListener);
    }
    
	public Selectable getSelectable() {
		return readerWriter;
	}

	public MessageProcessor getMessageProcessor() {
		return readerWriter;
	}
	
	@Override
	public void close() throws Exception {
		readerWriter.close();
	}

	@Override
	public void sendRequest(Request request) throws IOException {
	    
	    
		final int messageLength = readerWriter.writeRequest(request, byteOrder);

		if (protocolLog != null) {
            protocolLog.onSendRequest(messageLength, request);
        }
	}
}
