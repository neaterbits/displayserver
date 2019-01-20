package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;

public final class InternAtomReply extends XReply {

    private final ATOM atom;

    public InternAtomReply(CARD16 sequenceNumber, ATOM atom) {
        super(sequenceNumber);
    
        this.atom = atom;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("atom", atom);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)0));
        
        writeSequenceNumber(stream);
        
        stream.writeCARD32(new CARD32(0));
        
        stream.writeATOM(atom);
        
        stream.pad(20);
    }
}
