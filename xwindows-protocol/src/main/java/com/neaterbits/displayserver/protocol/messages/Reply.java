package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class Reply extends Message {

    private final CARD16 sequenceNumber;

    public Reply(CARD16 sequenceNumber) {
        
        Objects.requireNonNull(sequenceNumber);
        
        this.sequenceNumber = sequenceNumber;
    }

    public final CARD16 getSequenceNumber() {
        return sequenceNumber;
    }

    protected final void writeReplyHeader(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)1));
    }
    
    protected final void writeSequenceNumber(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD16(sequenceNumber);
    }
}
