package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class XClients implements XClientsConstAccess {

    private final List<XClient> clients;

    XClients() {
        this.clients = new ArrayList<>();
    }
    
    void addConnection(XClient client) {
        
        Objects.requireNonNull(client);
        
        if (clients.contains(client)) {
            throw new IllegalStateException();
        }
        
        clients.add(client);
    }
    
    @Override
    public Iterable<XClient> getClients() {
        return Collections.unmodifiableList(clients);
    }
}
