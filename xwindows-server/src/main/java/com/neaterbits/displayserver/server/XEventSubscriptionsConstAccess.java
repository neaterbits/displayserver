package com.neaterbits.displayserver.server;

interface XEventSubscriptionsConstAccess {

    Iterable<XWindowsConnectionState> getConnectionsInterestedInEvent(XWindowsWindow window, int event);
    
}
