package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.types.ATOM;

public final class GetSelectionOwner extends XRequest {

    private final ATOM selection;

    public static GetSelectionOwner decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new GetSelectionOwner(stream.readATOM());
    }
    
    public GetSelectionOwner(ATOM selection) {
        
        Objects.requireNonNull(selection);
        
        this.selection = selection;
    }

    public ATOM getSelection() {
        return selection;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap("selection", selection);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeATOM(selection);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_SELECTION_OWNER;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GetSelectionOwnerReply.class;
    }
}
