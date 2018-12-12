package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.displayserver.driver.common.Listeners;
import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.ServerToClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientConnectionError;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
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
	
	private int sequenceNumber;
	private final List<RequestWithReply> requestsWithReply;
	
	public XWindowsDriverConnection(int connectDisplay, NonBlockingChannelWriterLog writeLog, XWindowsClientProtocolLog protocolLog) throws IOException {

	    this.protocolLog = protocolLog;
	    
	    final int port = 6000 + connectDisplay;
	    
		this.readerWriter = new XWindowsClientReaderWriter(port, writeLog) {
		    
		    @Override
            protected boolean receivedInitialMessage() {
                return serverMessage != null;
            }

            @Override
		    public void onMessage(ByteBuffer byteBuffer, int messageLength) {

		        if (byteBuffer.get(byteBuffer.position()) == 0) {
		            
		            if (receivedInitialMessage()) {
		                try {
                            final Error error = readError(byteBuffer);
                            
                            final CARD16 sequenceNumber = error.getSequenceNumber();
                            
                            final RequestWithReply requestWithReply = getRequestWithReply(sequenceNumber);
                            
                            if (requestWithReply != null) {
                                requestWithReply.listener.onError(error);
                            }
                            
                        } catch (IOException ex) {
                            throw new IllegalStateException(ex);
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
        		            
        		            System.out.println("## received servermessage " + serverMessage);
        		            
        		            clientResourceIdAllocator = new ClientResourceIdAllocator(
        		                    serverMessage.getResourceIdBase().getValue(),
        		                    serverMessage.getResourceIdMask().getValue());
        		            
        		        }
        		        else {
        		            final ServerToClientMessage message = processMessage(
        		                    byteBuffer,
        		                    messageLength,
        		                    seq -> getRequestOpCodeFor(seq));
        		            
        		            final CARD16 sequenceNumber = message.getSequenceNumber();
        		            
                            final RequestWithReply requestWithReply = getRequestWithReply(sequenceNumber);
                            
                            if (requestWithReply != null) {
                                requestWithReply.listener.onReply((Reply)message);
                            }

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
		
		this.sequenceNumber = 1;
		this.requestsWithReply = new ArrayList<>();
		
		final List<XAuth> xAuths = XAuth.getXAuthInfo();
		
		final XAuth xAuthForTCPConnection = xAuths.stream()
		        .filter(xAuth -> xAuth.getAuthorizationProtocol().equals("MIT-MAGIC-COOKIE-1"))
		        .filter(xAuth -> xAuth.getCommunicationProtocol() == null)
		        .filter(xAuth -> xAuth.getDisplay() == connectDisplay)
		        .findFirst()
		        .orElse(null);

		/*
		if (xAuthForTCPConnection == null) {
		    throw new IllegalStateException();
		}
		*/
		
		final ClientMessage clientMessage = new ClientMessage(
		        new CARD8((short)'B'),
		        new CARD16(11),
		        new CARD16(0),
		        xAuthForTCPConnection != null ? xAuthForTCPConnection.getAuthorizationProtocol() : "",
		        xAuthForTCPConnection != null ? xAuthForTCPConnection.getAuthorizationData() : "".getBytes());
		
		readerWriter.writeEncodeable(clientMessage, byteOrder);
	}

	private int getRequestOpCodeFor(int sequenceNumber) {
	    
	    for (RequestWithReply requestWithReply : requestsWithReply) {
	        if (requestWithReply.sequenceNumber == sequenceNumber) {
	            return requestWithReply.opCode;
	        }
	    }
	    
	    throw new IllegalStateException();
	}

	private RequestWithReply getRequestWithReply(CARD16 sequenceNumber) {
        final Iterator<RequestWithReply> iter = requestsWithReply.iterator();
        
        RequestWithReply requestWithReply = null;
        
        while (iter.hasNext()) {
            final RequestWithReply seq = iter.next();
            
            if (seq.sequenceNumber == sequenceNumber.getValue()) {
                
                requestWithReply = seq;
                
                iter.remove();
                
                break;
            }
        }
	
        return requestWithReply;
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
	
	private com.neaterbits.displayserver.protocol.messages.Error readError(ByteBuffer byteBuffer) throws IOException {

	    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	    
	    final com.neaterbits.displayserver.protocol.messages.Error error = com.neaterbits.displayserver.protocol.messages.Error.decode(stream);

	    if (protocolLog != null) {
	        protocolLog.onReceivedError(error);
	    }
	    
	    return error;
	}
	
	
	private static ServerMessage processServerMessage(ByteBuffer byteBuffer, int messageLength) throws IOException {

	    final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
	    
	    return ServerMessage.decode(stream);
	}

	private ServerToClientMessage processMessage(
	        ByteBuffer byteBuffer,
	        int messageLength,
	        Function<Integer, Integer> getRequestOpCode) throws IOException {
	    
        final int opcode = byteBuffer.get();
        
        final ServerToClientMessage message;
        
        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        switch (opcode) {
        
        case 1:
            
            final int sequenceNumber = byteBuffer.getShort(byteBuffer.position() + 1);
            
            final int requestOpCode = getRequestOpCode.apply(sequenceNumber);
            
            final Reply reply;
            
            switch (requestOpCode) {
            case OpCodes.GET_IMAGE:
                reply = GetImageReply.decode(stream);
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown request opcode " + requestOpCode);
            }
            
            if (protocolLog != null) {
                protocolLog.onReceivedReply(reply);
            }

            message = reply;
            break;
        
        default:
            throw new UnsupportedOperationException("Unknown opcode " + opcode);
        }
        
        return message;
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
	public void sendRequest(Request request) {
	    sendRequestWithSequenceNumber(request);
	    
	    ++ sequenceNumber;
	}

    @Override
    public void sendRequestWaitReply(Request request, ReplyListener replyListener) {

        final int seq = this.sequenceNumber;
        
        requestsWithReply.add(new RequestWithReply(seq, request.getOpCode(), replyListener));

        ++ sequenceNumber;

        sendRequestWithSequenceNumber(request);
    }
    
    private void sendRequestWithSequenceNumber(Request request) {
        try {
            final int messageLength = readerWriter.writeRequest(request, byteOrder);
            
            if (protocolLog != null) {
                protocolLog.onSendRequest(messageLength, request);
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private static class RequestWithReply {
        
        private final int sequenceNumber;
        private final int opCode;
        private final ReplyListener listener;
        
        public RequestWithReply(int sequenceNumber, int opCode, ReplyListener listener) {

            Objects.requireNonNull(listener);
            
            this.sequenceNumber = sequenceNumber;
            this.opCode = opCode;
            this.listener = listener;
        }
    }
}
