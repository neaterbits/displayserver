package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;

public final class PolyFillRectangle extends PolyRectangleRequest {

    public static PolyFillRectangle decode(XWindowsProtocolInputStream stream) throws IOException {
        return PolyRectangleRequest.decode(stream, PolyFillRectangle::new);
    }
    
    public PolyFillRectangle(DRAWABLE drawable, GCONTEXT gc, RECTANGLE[] rectangles) {
        super(drawable, gc, rectangles);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_FILL_RECTANGLE;
    }
}
