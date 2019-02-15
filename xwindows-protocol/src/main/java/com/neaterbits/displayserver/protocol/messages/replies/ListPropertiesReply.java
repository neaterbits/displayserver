package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class ListPropertiesReply extends XReply {

    private final ATOM [] atoms;

    public ListPropertiesReply(CARD16 sequenceNumber, ATOM[] atoms) {
        super(sequenceNumber);

        Objects.requireNonNull(atoms);
        
        this.atoms = atoms;
    }

    public ATOM[] getAtoms() {
        return atoms;
    }

    
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "atoms", Arrays.toString(atoms)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, atoms.length);
        
        stream.writeCARD16(new CARD16(atoms.length));
        
        stream.pad(22);
        
        encodeArray(atoms, stream);
    }
}
