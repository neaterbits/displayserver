package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class Event extends ServerToClientMessage {

    public Event(CARD16 sequenceNumber) {
        super(sequenceNumber);
    }

    protected final void writeEventCode(XWindowsProtocolOutputStream stream, int code) throws IOException {
        stream.writeBYTE(new BYTE((byte)code));
    }
}
