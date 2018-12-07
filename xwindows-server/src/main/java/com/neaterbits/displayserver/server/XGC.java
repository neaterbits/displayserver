package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;

final class XGC {

    private GCAttributes attributes;

    public XGC(GCAttributes attributes) {

        Objects.requireNonNull(attributes);
        
        this.attributes = attributes;
    }

    public GCAttributes getAttributes() {
        return attributes;
    }
}
