package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.CURSOR;

public final class CursorException extends ProtocolException {

    private static final long serialVersionUID = 1L;

    private final CURSOR cursor;

    public CursorException(String message, CURSOR cursor) {
        super(message);

        this.cursor = cursor;
    }

    public CURSOR getCursor() {
        return cursor;
    }
}
