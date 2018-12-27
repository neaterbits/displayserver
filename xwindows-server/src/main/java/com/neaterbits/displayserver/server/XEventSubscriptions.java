package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.xwindows.model.XWindow;

final class XEventSubscriptions implements XEventSubscriptionsConstAccess {

    @Override
    public Iterable<XClient> getClientsInterestedInEvent(XWindow window, int event) {
        throw new UnsupportedOperationException("TODO");
    }
}
