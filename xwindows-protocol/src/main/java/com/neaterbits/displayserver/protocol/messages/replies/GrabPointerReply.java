package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class GrabPointerReply extends XReply {

    private final BYTE status;

    public GrabPointerReply(CARD16 sequenceNumber, BYTE status) {
        super(sequenceNumber);
    
        Objects.requireNonNull(status);
        
        this.status = status;
    }

    public BYTE getStatus() {
        return status;
    }
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap("status", status);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(status);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 0);
        
        stream.pad(24);
    }
}
