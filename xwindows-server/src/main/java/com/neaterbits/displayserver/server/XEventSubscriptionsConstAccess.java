package com.neaterbits.displayserver.server;

import java.util.function.Function;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public interface XEventSubscriptionsConstAccess {

    default void sendEventToSubscribing(
            XWindow xWindow,
            int eventCode,
            Function<XClientOps, Event> makeEvent) {
        
        sendEventToSubscribing(xWindow.getWINDOW(), eventCode, makeEvent);
    }
        
    default void sendEventToSubscribing(
            WINDOW window,
            int eventCode,
            Function<XClientOps, Event> makeEvent) {
        
        
        for (XClientOps client : getClientsInterestedInEvent(window, eventCode)) {
            
            final Event event = makeEvent.apply(client);
            
            client.sendEvent(event);
        }
    }

    
    Iterable<XClientOps> getClientsInterestedInEvent(WINDOW window, int event);
    
    XClientOps getSingleClientInterestedInEvent(WINDOW window, int event);
}
