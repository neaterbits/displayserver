package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class FreeGC extends XRequest {

    private final GCONTEXT gc;

    public static FreeGC decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new FreeGC(stream.readGCONTEXT());
    }
    
    public FreeGC(GCONTEXT gc) {
        this.gc = gc;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("gc", gc);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeGCONTEXT(gc);
    }

    @Override
    public int getOpCode() {
        return OpCodes.FREE_GC;
    }

    public GCONTEXT getGContext() {
        return gc;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
