package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.driver.common.Listeners;
import com.neaterbits.displayserver.driver.xwindows.common.messaging.XWindowsDriverMessageSending;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.render.cairo.xcb.XCBConnection;
import com.neaterbits.displayserver.render.cairo.xcb.XCBVisual;

public final class XWindowsDriverConnection
		implements AutoCloseable, XWindowsRequestSender {

    private final int connectDisplay;
    
	private final XWindowsMessaging messaging;
	
	private final Listeners<XWindowsReplyListener> replyListeners;
	private final Listeners<XWindowsMessageListener> eventListeners;
	
	private ClientResourceIdAllocator clientResourceIdAllocator;
	
	private final XCBConnection xcbConnection;
	private final XCBVisual xcbVisual;
	
	public XWindowsDriverConnection(
	        int connectDisplay,
	        XCBConnection xcbConnection,
	        XWindowsNetworkFactory networkFactory,
	        XWindowsClientProtocolLog protocolLog) throws IOException {

	    Objects.requireNonNull(networkFactory);
	    Objects.requireNonNull(xcbConnection);
	    
	    this.connectDisplay = connectDisplay;
	    this.xcbConnection = xcbConnection;
	    
        final XWindowsMessageListener messageListener = new XWindowsMessageListener() {
            
            @Override
            public void onInitialMessage(ServerMessage initialMessage) {
                clientResourceIdAllocator = new ClientResourceIdAllocator(
                        initialMessage.getResourceIdBase().getValue(),
                        initialMessage.getResourceIdMask().getValue());
            }
        };
        
        this.messaging = new XWindowsDriverMessageSending(
	            networkFactory,
	            messageListener,
	            protocolLog);
	    
		this.replyListeners = new Listeners<>();
		this.eventListeners = new Listeners<>();
		
		this.xcbVisual = xcbConnection.getSetup().getScreens()
	            .get(0)
	            .getDepths()
	            .stream()
	            .filter(depth -> depth.getDepth() == 24)
	            .findFirst()
	            .get()
	            .getVisuals()
	            .get(0);
	}

	public XCBConnection getXCBConnection() {
        return xcbConnection;
    }

    public XCBVisual getXCBVisual() {
        return xcbVisual;
    }

    @Override
    public void sendRequest(Request request) {
	    messaging.sendRequest(request);
    }

    @Override
    public void sendRequestWaitReply(Request request, ReplyListener replyListener) {
        messaging.sendRequestWaitReply(request, replyListener);
    }

    public int getConnectDisplay() {
        return connectDisplay;
    }
	
	public ServerMessage getServerMessage() {
        return messaging.getServerMessage();
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
	
    public void registerEventListener(XWindowsMessageListener eventListener) {
        eventListeners.registerListener(eventListener);
    }

    public void deregisterEventListener(XWindowsMessageListener eventListener) {
        eventListeners.deregisterListener(eventListener);
    }

    @Override
	public void close() throws Exception {
		messaging.close();
	}
}
