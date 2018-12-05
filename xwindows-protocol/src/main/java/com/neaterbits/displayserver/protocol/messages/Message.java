package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;

public abstract class Message implements Encodeable {
	
    protected static void writeUnusedByte(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)0));
    }

    protected static void readUnusedByte(XWindowsProtocolInputStream stream) throws IOException {
        stream.readBYTE();
    }
}
