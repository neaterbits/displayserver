package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class ColormapException extends ProtocolException {
    
    private static final long serialVersionUID = 1L;
    
    private final COLORMAP colormap;

    public ColormapException(String message, COLORMAP colormap) {
        super(message);

        this.colormap = colormap;
    }

    public COLORMAP getColormap() {
        return colormap;
    }
}
