package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;

public final class SetClipRectangles extends XRequest {

    private final BYTE ordering;
    private final GCONTEXT gc;
    
    private final INT16 clipXOrigin;
    private final INT16 clipYOrigin;
    
    private final RECTANGLE [] rectangles;

    public static SetClipRectangles decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE ordering = stream.readBYTE();
        
        final CARD16 requestLength = readRequestLength(stream);
        
        final int numRectangles = (requestLength.getValue() - 3) / 2;
        
        return new SetClipRectangles(
                ordering,
                stream.readGCONTEXT(),
                stream.readINT16(),
                stream.readINT16(),
                decodeArray(stream, new RECTANGLE[numRectangles], RECTANGLE::decode));
    }
    
    public SetClipRectangles(BYTE ordering, GCONTEXT gc, INT16 clipXOrigin, INT16 clipYOrigin, RECTANGLE[] rectangles) {

        Objects.requireNonNull(ordering);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(clipXOrigin);
        Objects.requireNonNull(clipYOrigin);
        Objects.requireNonNull(rectangles);
        
        this.ordering = ordering;
        this.gc = gc;
        this.clipXOrigin = clipXOrigin;
        this.clipYOrigin = clipYOrigin;
        this.rectangles = rectangles;
    }

    public BYTE getOrdering() {
        return ordering;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public INT16 getClipXOrigin() {
        return clipXOrigin;
    }

    public INT16 getClipYOrigin() {
        return clipYOrigin;
    }

    public RECTANGLE[] getRectangles() {
        return rectangles;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "ordering", ordering,
                "gc", gc,
                "clipXOrigin", clipXOrigin,
                "clipYOrigin", clipYOrigin,
                "rectangles", Arrays.toString(rectangles)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        stream.writeBYTE(ordering);
        
        writeRequestLength(stream, 3 + 2 * rectangles.length);
        
        stream.writeGCONTEXT(gc);
        
        stream.writeINT16(clipXOrigin);
        stream.writeINT16(clipYOrigin);
        
        encodeArray(rectangles, stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SET_CLIP_RECTANGLES;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
