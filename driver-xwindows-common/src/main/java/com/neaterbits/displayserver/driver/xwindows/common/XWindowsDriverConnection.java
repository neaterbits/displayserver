package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.driver.common.DisplayDeviceId;
import com.neaterbits.displayserver.driver.common.Listeners;
import com.neaterbits.displayserver.driver.xwindows.common.messaging.XWindowsDriverMessageSending;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.render.cairo.xcb.XCBConnection;
import com.neaterbits.displayserver.render.cairo.xcb.XCBVisual;

public final class XWindowsDriverConnection
		implements AutoCloseable, XWindowsRequestSender {

    private final int connectDisplay;
    
	private final XWindowsMessaging messaging;
	
	private final Listeners<XWindowsReplyListener> replyListeners;
	private final Listeners<XWindowsEventListener> eventListeners;
	
	private final XCBConnection xcbConnection;
	private final XCBVisual xcbVisual;
	
	private final Map<WINDOW, DisplayDeviceId> windowToDisplayDeviceId;
	
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

            }
        };
        
        final XWindowsEventListener eventListener = new XWindowsEventListener() {
            
            @Override
            public void onEvent(XEvent event) {
                eventListeners.triggerEvent(event, XWindowsEventListener::onEvent);
            }
        };
        
        this.messaging = new XWindowsDriverMessageSending(
	            networkFactory,
	            messageListener,
	            eventListener,
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
		
		this.windowToDisplayDeviceId = new HashMap<>();
	}

	public void addWindowToDisplayDevice(WINDOW window, DisplayDeviceId displayDeviceId) {
	    
	    Objects.requireNonNull(window);
	    Objects.requireNonNull(displayDeviceId);
	
	    if (windowToDisplayDeviceId.containsKey(window)) {
	        throw new IllegalArgumentException();
	    }
	    
	    if (windowToDisplayDeviceId.containsValue(displayDeviceId)) {
	        throw new IllegalArgumentException();
	    }
	    
	    windowToDisplayDeviceId.put(window, displayDeviceId);
	}
	
	public DisplayDeviceId getDisplayDeviceId(WINDOW window) {
	    return windowToDisplayDeviceId.get(window);
	}
	
	
	public boolean isPolling() {
	    return messaging.isPolling();
	}
	
	public void pollForEvents() {
	    messaging.pollForEvents();
	}
	
	public void flush() {
	    xcbConnection.flush();
	}
	
	public XCBConnection getXCBConnection() {
        return xcbConnection;
    }

    public XCBVisual getXCBVisual() {
        return xcbVisual;
    }

    @Override
    public void sendRequest(XRequest request) {
	    messaging.sendRequest(request);
    }

    @Override
    public void sendRequestWaitReply(XRequest request, ReplyListener replyListener) {
        messaging.sendRequestWaitReply(request, replyListener);
    }

    public int getConnectDisplay() {
        return connectDisplay;
    }
	
	public ServerMessage getServerMessage() {
        return messaging.getServerMessage();
    }
	
	public int allocateResourceId() {
	    return messaging.allocateResourceId();
	}

	public void freeResourceId(int resourceId) {
	    messaging.freeResourceId(resourceId);
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

    @Override
	public void close() throws Exception {
		messaging.close();
	}
}
