package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.ATOM;

public final class AtomException extends ProtocolException {

    private static final long serialVersionUID = 1L;

    private final ATOM atom;
    
    public AtomException(String message, ATOM atom) {
        super(message);
        
        this.atom = atom;
    }

    public ATOM getAtom() {
        return atom;
    }
}
