package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class ListFontsWithInfoLastReply extends XReply {

    public ListFontsWithInfoLastReply(CARD16 sequenceNumber) {
        super(sequenceNumber);

    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return new Object[0];
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)0));
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 7);
        
        stream.pad(52);
    }
}
