package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class XRequest extends XMessage {

    public abstract int getOpCode();
    
    public abstract Class<? extends XReply> getReplyClass();
    
    protected final void writeOpCode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)getOpCode()));
    }
    
    protected static void writeRequestLength(XWindowsProtocolOutputStream stream, int requestLength) throws IOException {
        stream.writeCARD16(new CARD16(requestLength));
    }

    protected static CARD16 readRequestLength(XWindowsProtocolInputStream stream) throws IOException {
        return stream.readCARD16();
    }
}
