package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.xwindows.model.XWindow;

interface XEventSubscriptionsConstAccess {

    Iterable<XClient> getClientsInterestedInEvent(XWindow window, int event);
    
}
