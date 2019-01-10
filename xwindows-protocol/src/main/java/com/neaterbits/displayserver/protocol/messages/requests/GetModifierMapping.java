package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;

public final class GetModifierMapping extends Request {

    public static GetModifierMapping decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new GetModifierMapping();
    }
    
    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 1);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_MODIFIER_MAPPING;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return GetModifierMappingReply.class;
    }
}
