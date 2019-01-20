package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;

public abstract class XReply extends XServerToClientMessage {

    public XReply(CARD16 sequenceNumber) {
        super(sequenceNumber);
    }

    protected final void writeReplyHeader(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)1));
    }

    protected static void writeReplyLength(XWindowsProtocolOutputStream stream, long replyLength) throws IOException {
        stream.writeCARD32(new CARD32(replyLength));
    }
}
