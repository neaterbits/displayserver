package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;

public final class SetCloseDownMode extends Request {

    private final BYTE mode;

    public static SetCloseDownMode decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE mode = stream.readBYTE();
    
        readRequestLength(stream);
        
        return new SetCloseDownMode(mode);
    }
    
    public SetCloseDownMode(BYTE mode) {

        Objects.requireNonNull(mode);
        
        this.mode = mode;
    }

    public BYTE getMode() {
        return mode;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("mode", mode);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(mode);
        
        writeRequestLength(stream, 1);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SET_CLOSE_DOWN_MODE;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
