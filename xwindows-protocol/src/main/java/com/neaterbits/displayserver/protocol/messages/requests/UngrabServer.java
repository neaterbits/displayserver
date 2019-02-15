package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;

public final class UngrabServer extends XRequest {

    public static UngrabServer decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new UngrabServer();
    }
    
    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 1);
    }

    @Override
    public int getOpCode() {
        return OpCodes.UNGRAB_SERVER;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
