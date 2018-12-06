package com.neaterbits.displayserver.server;

final class XEventSubscriptions implements XEventSubscriptionsConstAccess {

    @Override
    public Iterable<XWindowsConnectionState> getConnectionsInterestedInEvent(XWindow window, int event) {
        throw new UnsupportedOperationException("TODO");
    }
}
