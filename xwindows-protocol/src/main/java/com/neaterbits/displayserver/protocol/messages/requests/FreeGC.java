package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class FreeGC extends Request {

    private final GCONTEXT gc;

    public FreeGC(GCONTEXT gc) {
        this.gc = gc;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeGCONTEXT(gc);
    }

    public GCONTEXT getGc() {
        return gc;
    }
}
