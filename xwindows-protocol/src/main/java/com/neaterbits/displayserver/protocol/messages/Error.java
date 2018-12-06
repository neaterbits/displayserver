package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class Error extends Message {

    private final BYTE code;
    private final CARD16 sequenceNumber;
    private final CARD32 value;
    private final CARD8 opcode;

    public Error(BYTE code, CARD16 sequenceNumber, CARD32 value, CARD8 opcode) {
        this.code = code;
        this.sequenceNumber = sequenceNumber;
        this.value = value;
        this.opcode = opcode;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("code", code, "sequenceNumber", sequenceNumber, "value", value, "opcode", opcode);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        stream.writeBYTE(new BYTE((byte)0));
        
        stream.writeBYTE(code);
        
        stream.writeCARD16(sequenceNumber);
        
        stream.writeCARD32(value);
        
        stream.writeCARD16(new CARD16(0));
        
        stream.writeCARD8(opcode);
        
        stream.pad(21);
    }
}
