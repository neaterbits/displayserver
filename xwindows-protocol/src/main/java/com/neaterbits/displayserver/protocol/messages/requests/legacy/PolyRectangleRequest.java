package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;

public abstract class PolyRectangleRequest extends PolyRequest<RECTANGLE> {

    private static final int CARD32_PER_ENTRY = 2;
    
    public static <REQUEST extends PolyRectangleRequest>
    REQUEST decode(XWindowsProtocolInputStream stream, CreatePolyRequestNoInitialByte<RECTANGLE, REQUEST> createPolyRequest) throws IOException {
        
        return PolyRequest.decodeNoInitialByte(
                stream,
                CARD32_PER_ENTRY,
                RECTANGLE[]::new,
                RECTANGLE::decode,
                createPolyRequest);
    }
    
    public PolyRectangleRequest(DRAWABLE drawable, GCONTEXT gc, RECTANGLE[] rectangles) {
        super(drawable, gc, rectangles);
    }

    public final RECTANGLE[] getRectangles() {
        return getList();
    }

    @Override
    public final Object[] getDebugParams() {
        return wrap(
                "drawable", getDrawable(),
                "gc", getGC(),
                "rectangles", getListDebugParam()
        );
    }

    @Override
    public final void encode(XWindowsProtocolOutputStream stream) throws IOException {

        super.encode(stream, CARD32_PER_ENTRY);
        
    }
}
