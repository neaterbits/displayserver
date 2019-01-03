package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;

public final class PolyFillRectangle extends Request {

    private final DRAWABLE drawable;
    private final GCONTEXT gc;
    
    private final RECTANGLE [] rectangles;

    public static PolyFillRectangle decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 requestLength = stream.readCARD16();
        
        final int numRectangles = (requestLength.getValue() - 3) / 2;
        
        return new PolyFillRectangle(
                stream.readDRAWABLE(),
                stream.readGCONTEXT(),
                decodeArray(stream, new RECTANGLE[numRectangles], RECTANGLE::decode));
    }
    
    public PolyFillRectangle(DRAWABLE drawable, GCONTEXT gc, RECTANGLE[] rectangles) {

        Objects.requireNonNull(drawable);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(rectangles);
        
        this.drawable = drawable;
        this.gc = gc;
        this.rectangles = rectangles;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public RECTANGLE[] getRectangles() {
        return rectangles;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "drawable", drawable,
                "gc", gc,
                "rectangles", rectangles
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3 + rectangles.length);
        
        encodeArray(rectangles, stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_FILL_RECTANGLE;
    }
}
