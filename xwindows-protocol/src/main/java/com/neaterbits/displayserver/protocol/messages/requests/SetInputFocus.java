package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class SetInputFocus extends Request {

    private final BYTE revertTo;
    private final WINDOW focus;
    private final TIMESTAMP time;
    
    public static SetInputFocus decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE revertTo = stream.readBYTE();
        
        readRequestLength(stream);
        
        return new SetInputFocus(
                revertTo,
                stream.readWINDOW(),
                stream.readTIMESTAMP());
    }
    
    public SetInputFocus(BYTE revertTo, WINDOW focus, TIMESTAMP time) {

        Objects.requireNonNull(revertTo);
        Objects.requireNonNull(focus);
        Objects.requireNonNull(time);
        
        this.revertTo = revertTo;
        this.focus = focus;
        this.time = time;
    }

    public BYTE getRevertTo() {
        return revertTo;
    }

    public WINDOW getFocus() {
        return focus;
    }

    public TIMESTAMP getTime() {
        return time;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "revertTo", revertTo,
                "focus", focus,
                "time", time
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(revertTo);
        
        writeRequestLength(stream, 3);
        
        stream.writeWINDOW(focus);
        
        stream.writeTIMESTAMP(time);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SET_INPUT_FOCUS;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
