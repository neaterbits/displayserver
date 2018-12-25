package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.RESOURCE;

public final class FontException extends ProtocolException {

    private static final long serialVersionUID = 1L;

    private final RESOURCE font;
    
    public FontException(String message, RESOURCE font) {
        super(message);
        
        this.font = font;
    }

    public RESOURCE getFont() {
        return font;
    }
}
