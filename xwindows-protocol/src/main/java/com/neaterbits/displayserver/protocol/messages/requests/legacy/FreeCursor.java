package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.CURSOR;

public final class FreeCursor extends XRequest {

    private final CURSOR cursor;

    public static FreeCursor deccode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new FreeCursor(stream.readCURSOR());
    }
    
    public FreeCursor(CURSOR cursor) {

        Objects.requireNonNull(cursor);
        
        this.cursor = cursor;
    }

    public CURSOR getCursor() {
        return cursor;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap("cursor", cursor);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
    
        stream.writeCURSOR(cursor);
    }

    @Override
    public int getOpCode() {
        return OpCodes.FREE_CURSOR;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
