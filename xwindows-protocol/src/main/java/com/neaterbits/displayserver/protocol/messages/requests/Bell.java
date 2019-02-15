package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.INT8;

public final class Bell extends XRequest {

    private final INT8 percent;

    public static final Bell decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final INT8 percent = stream.readINT8();
        
        readRequestLength(stream);
        
        return new Bell(percent);
    }
    
    public Bell(INT8 percent) {

        Objects.requireNonNull(percent);

        this.percent = percent;
    }

    public INT8 getPercent() {
        return percent;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeINT8(percent);
        
        writeRequestLength(stream, 1);
    }

    @Override
    public int getOpCode() {
        return OpCodes.BELL;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
