package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GetInputFocusReply extends XReply {

    private final BYTE revertTo;
    private final WINDOW focus;
    
    public static GetInputFocusReply decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE revertTo = stream.readBYTE();
        
        final CARD16 sequenceNumber = stream.readCARD16();
        
        readReplyLength(stream);
        
        final WINDOW focus = stream.readWINDOW();
        
        stream.readPad(20);
        
        return new GetInputFocusReply(sequenceNumber, revertTo, focus);
    }
    
    public GetInputFocusReply(CARD16 sequenceNumber, BYTE revertTo, WINDOW focus) {
        super(sequenceNumber);
        
        Objects.requireNonNull(revertTo);
        Objects.requireNonNull(focus);
        
        this.revertTo = revertTo;
        this.focus = focus;
    }
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap("revertTo", revertTo, "focus", focus);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeBYTE(revertTo);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 0);
        
        stream.writeWINDOW(focus);
        
        stream.pad(20);
    }
}
