package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.neaterbits.displayserver.driver.common.Listeners;
import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class XWindowsDriverConnection
		implements AutoCloseable, XWindowsRequestSender {

    private final int connectDisplay;
    
	private final XWindowsChannelReaderWriter readerWriter;
	
	private final Listeners<XWindowsReplyListener> replyListeners;
	private final Listeners<XWindowsEventListener> eventListeners;
	
	private ClientResourceIdAllocator clientResourceIdAllocator;
	
	private final XWindowsDriverMessageProcessing messageProcessing;
	
	public XWindowsDriverConnection(int connectDisplay, NonBlockingChannelWriterLog writeLog, XWindowsClientProtocolLog protocolLog) throws IOException {

	    this.connectDisplay = connectDisplay;
	    
	    final int port = 6000 + connectDisplay;
	    
	    this.messageProcessing = new XWindowsDriverMessageProcessing(protocolLog);
	    
		this.readerWriter = new XWindowsClientReaderWriter(port, writeLog) {
		    
		    @Override
            protected boolean receivedInitialMessage() {
                return messageProcessing.receivedInitialMessage();
            }

            @Override
		    public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                final ServerMessage initialMessage = messageProcessing.onMessage(byteBuffer, messageLength);
                
                if (initialMessage != null) {
                    clientResourceIdAllocator = new ClientResourceIdAllocator(
                            initialMessage.getResourceIdBase().getValue(),
                            initialMessage.getResourceIdMask().getValue());
                    
                }
		    }
		};
		
		this.replyListeners = new Listeners<>();
		this.eventListeners = new Listeners<>();
		
		final XAuth xAuthForTCPConnection = XAuth.getXAuthInfo(connectDisplay, "MIT-MAGIC-COOKIE-1");
		
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
		
		readerWriter.writeEncodeable(clientMessage, messageProcessing.getByteOrder());
	}

	@Override
    public void sendRequest(Request request) {
	    messageProcessing.sendRequest(request, readerWriter);
    }

    @Override
    public void sendRequestWaitReply(Request request, ReplyListener replyListener) {
        messageProcessing.sendRequestWaitReply(request, replyListener, readerWriter);
    }

    public int getConnectDisplay() {
        return connectDisplay;
    }
	
	public ServerMessage getServerMessage() {
        return messageProcessing.getServerMessage();
    }
	
	public int allocateResourceId() {
	    return clientResourceIdAllocator.allocateResourceId();
	}

	public void freeResourceId(int resourceId) {
	    clientResourceIdAllocator.freeResourceId(resourceId);
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
}
