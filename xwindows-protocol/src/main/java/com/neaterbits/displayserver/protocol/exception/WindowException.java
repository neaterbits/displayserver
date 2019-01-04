package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class WindowException extends ProtocolException {

    private static final long serialVersionUID = 1L;

    private final WINDOW window;

    public WindowException(String message, WINDOW window) {
        super(message);
    
        this.window = window;
    }

    public WINDOW getWindow() {
        return window;
    }
}
