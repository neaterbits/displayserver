package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.SEGMENT;

public final class PolySegment extends PolyRequest<SEGMENT> {

    private static final int CARD32_PER_ENTRY = 2;
    
    public static PolySegment decode(XWindowsProtocolInputStream stream) throws IOException {
        return PolyRequest.decodeNoInitialByte(
                stream,
                CARD32_PER_ENTRY,
                SEGMENT[]::new,
                SEGMENT::decode,
                PolySegment::new);
    }
    
    public PolySegment(DRAWABLE drawable, GCONTEXT gc, SEGMENT[] segments) {
        super(drawable, gc, segments);
    }

    public SEGMENT [] getSegments() {
        return getList();
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "drawable", getDrawable(),
                "gc", getGC(),
                "segments", getListDebugParam()
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        super.encode(stream, CARD32_PER_ENTRY);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_SEGMENT;
    }
}
