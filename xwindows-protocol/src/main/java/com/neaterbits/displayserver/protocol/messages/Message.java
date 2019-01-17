package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;

public abstract class Message extends Encodeable {
    
    protected static void writeUnusedByte(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)0));
    }

    protected static void readUnusedByte(XWindowsProtocolInputStream stream) throws IOException {
        stream.readBYTE();
    }

    protected static void readUnusedCARD16(XWindowsProtocolInputStream stream) throws IOException {
        stream.readCARD16();
    }

    protected static CARD32 readReplyLength(XWindowsProtocolInputStream stream) throws IOException {
        return stream.readCARD32();
    }

    protected static void writeUnusedCARD16(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD16(new CARD16(0));
    }

    protected static void writeStrings(XWindowsProtocolOutputStream stream, String [] strings) throws IOException {
        for (String string : strings) {
            stream.writeSTRING8(string);
            
            stream.writeBYTE(new BYTE((byte)0));
        }
    }
}
