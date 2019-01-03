package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.POINT;

public final class PolyPoint extends PolyPointRequest {

    public static PolyPoint decode(XWindowsProtocolInputStream stream) throws IOException {
        return PolyPointRequest.decode(stream, PolyPoint::new);
    }
    
    public PolyPoint(BYTE coordinateMode, DRAWABLE drawable, GCONTEXT gc, POINT[] points) {
        super(coordinateMode, drawable, gc, points);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_POINT;
    }
}
