package com.neaterbits.displayserver.server;

final class XEventSubscriptions implements XEventSubscriptionsConstAccess {

    @Override
    public Iterable<XWindowsConnectionState> getConnectionsInterestedInEvent(XWindowsWindow window, int event) {
        throw new UnsupportedOperationException("TODO");
    }
}
