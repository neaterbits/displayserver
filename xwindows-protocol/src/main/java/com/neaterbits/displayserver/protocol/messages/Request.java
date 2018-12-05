package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;

public abstract class Request extends Message {

    protected final void writeOpCode(XWindowsProtocolOutputStream stream, int opCode) throws IOException {
        stream.writeBYTE(new BYTE((byte)opCode));
    }
}
