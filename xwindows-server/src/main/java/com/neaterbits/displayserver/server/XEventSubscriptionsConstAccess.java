package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public interface XEventSubscriptionsConstAccess {
    
    Iterable<XClientOps> getClientsInterestedInEvent(WINDOW window, int event);
    
    XClientOps getSingleClientInterestedInEvent(WINDOW window, int event);
}
