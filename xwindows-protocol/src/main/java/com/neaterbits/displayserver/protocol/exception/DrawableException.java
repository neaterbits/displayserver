package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;

public final class DrawableException extends ProtocolException {

    private static final long serialVersionUID = 1L;
    
    private final DRAWABLE drawable;

    public DrawableException(String message, DRAWABLE drawable) {
        super(message);
    
        this.drawable = drawable;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }
}
