package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class GContextException extends ProtocolException {

    private static final long serialVersionUID = 1L;
    
    private final GCONTEXT gc;

    public GContextException(String message, GCONTEXT gc) {
        super(message);
    
        this.gc = gc;
    }

    public GCONTEXT getGContext() {
        return gc;
    }
}
