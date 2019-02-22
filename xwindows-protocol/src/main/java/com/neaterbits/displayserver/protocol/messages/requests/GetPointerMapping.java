package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetPointerMappingReply;

public final class GetPointerMapping extends XRequest {

    public static GetPointerMapping decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new GetPointerMapping();
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 1);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_POINTER_MAPPING;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GetPointerMappingReply.class;
    }
}
