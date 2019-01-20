package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class PixmapException extends ProtocolException {

    private static final long serialVersionUID = 1L;
    
    private final PIXMAP pixmap;
    
    public PixmapException(String message, PIXMAP pixmap) {
        super(message);
    
        this.pixmap = pixmap;
    }

    public PIXMAP getPixmap() {
        return pixmap;
    }
}
