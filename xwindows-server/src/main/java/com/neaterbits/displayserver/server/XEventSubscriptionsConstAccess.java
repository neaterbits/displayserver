package com.neaterbits.displayserver.server;

interface XEventSubscriptionsConstAccess {

    Iterable<XClient> getClientsInterestedInEvent(XWindow window, int event);
    
}
