package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;

public final class XGC {

    private XGCAttributes attributes;

    public XGC(XGCAttributes attributes) {

        Objects.requireNonNull(attributes);
        
        this.attributes = attributes;
    }

    public XGCAttributes getAttributes() {
        return attributes;
    }
}
