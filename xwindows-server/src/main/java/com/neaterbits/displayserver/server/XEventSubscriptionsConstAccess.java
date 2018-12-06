package com.neaterbits.displayserver.server;

interface XEventSubscriptionsConstAccess {

    Iterable<XWindowsConnectionState> getConnectionsInterestedInEvent(XWindow window, int event);
    
}
