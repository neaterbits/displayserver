package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;

public final class PolyRectangle extends PolyRectangleRequest {

    public static PolyRectangle decode(XWindowsProtocolInputStream stream) throws IOException {
        return PolyRectangleRequest.decode(stream, PolyRectangle::new);
    }

    public PolyRectangle(DRAWABLE drawable, GCONTEXT gc, RECTANGLE [] rectangles) {
        super(drawable, gc, rectangles);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_RECTANGLE;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
