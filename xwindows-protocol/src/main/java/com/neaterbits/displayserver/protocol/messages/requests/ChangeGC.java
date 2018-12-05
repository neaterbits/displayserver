package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class ChangeGC extends Request {

    private final GCONTEXT gc;
    private final GCAttributes attributes;
    
    public ChangeGC(GCONTEXT gc, GCAttributes attributes) {
        this.gc = gc;
        this.attributes = attributes;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeGCONTEXT(gc);
        attributes.encode(stream);
    }

    public GCONTEXT getGc() {
        return gc;
    }

    public GCAttributes getAttributes() {
        return attributes;
    }
}
