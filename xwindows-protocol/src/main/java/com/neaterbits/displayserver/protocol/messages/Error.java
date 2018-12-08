package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
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

    public static Error decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE errorMarker = stream.readBYTE();
        
        if (errorMarker.getValue() != 0) {
            throw new IllegalStateException();
        }
        
        final BYTE code = stream.readBYTE();
        final CARD16 sequenceNumber = stream.readCARD16();
        final CARD32 value = stream.readCARD32();
        
        stream.readCARD16();
        
        final CARD8 opcode = stream.readCARD8();
        
        stream.readPad(21);
        
        return new Error(code, sequenceNumber, value, opcode);
    }
    
    @Override
    public Object[] getDebugParams() {
        
        return wrap(
                "code", Errors.name(code),
                "sequenceNumber", sequenceNumber,
                "value", String.format("0x%08x", value.getValue()),
                "opcode", opcode);
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
