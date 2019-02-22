package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class SetSelectionOwner extends XRequest {

    private final WINDOW owner;
    private final ATOM selection;
    private final TIMESTAMP time;
    
    public static SetSelectionOwner decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new SetSelectionOwner(
                stream.readWINDOW(),
                stream.readATOM(),
                stream.readTIMESTAMP());
    }
    
    public SetSelectionOwner(WINDOW owner, ATOM selection, TIMESTAMP time) {

        Objects.requireNonNull(owner);
        Objects.requireNonNull(selection);
        Objects.requireNonNull(time);
        
        this.owner = owner;
        this.selection = selection;
        this.time = time;
    }

    public WINDOW getOwner() {
        return owner;
    }

    public ATOM getSelection() {
        return selection;
    }

    public TIMESTAMP getTime() {
        return time;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "owner", owner,
                "selection", selection,
                "time", time
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 4);
        
        stream.writeWINDOW(owner);
        stream.writeATOM(selection);
        stream.writeTIMESTAMP(time);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SET_SELECTION_OWNER;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
