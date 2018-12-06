package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class XConnections implements XConnectionsConstAccess {

    private final List<XWindowsConnectionState> connections;

    XConnections() {
        this.connections = new ArrayList<>();
    }
    
    void addConnection(XWindowsConnectionState connection) {
        
        Objects.requireNonNull(connection);
        
        if (connections.contains(connection)) {
            throw new IllegalStateException();
        }
        
        connections.add(connection);
    }
    
    @Override
    public Iterable<XWindowsConnectionState> getConnections() {
        return Collections.unmodifiableList(connections);
    }
}
