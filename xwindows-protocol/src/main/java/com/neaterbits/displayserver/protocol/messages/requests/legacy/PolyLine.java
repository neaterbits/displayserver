package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.POINT;

public final class PolyLine extends Request {

    private final BYTE coordinateMode;
    private final DRAWABLE drawable;
    private final GCONTEXT gc;
    private final POINT [] points;
    
    
    public static PolyLine decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE coordinateMode = stream.readBYTE();
        
        final CARD16 requestLength = stream.readCARD16();
        
        final int numPoints = requestLength.getValue() - 3;
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        
        final POINT [] points = new POINT[numPoints];
        
        for (int i = 0; i < numPoints; ++ i) {
            points[i] = stream.readPOINT();
        }
        
        return new PolyLine(coordinateMode, drawable, gc, points);
    }
    
    public PolyLine(BYTE coordinateMode, DRAWABLE drawable, GCONTEXT gc, POINT[] points) {

        Objects.requireNonNull(coordinateMode);
        Objects.requireNonNull(drawable);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(points);
        
        this.coordinateMode = coordinateMode;
        this.drawable = drawable;
        this.gc = gc;
        this.points = points;
    }

    public BYTE getCoordinateMode() {
        return coordinateMode;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public POINT[] getPoints() {
        return points;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "coordinateMode", coordinateMode,
                "drawable", drawable,
                "gc", gc,
                "points", Arrays.toString(points)
                
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(coordinateMode);
        
        writeRequestLength(stream, 3 + points.length);
        
        stream.writeDRAWABLE(drawable);
        stream.writeGCONTEXT(gc);
        
        for (POINT point : points) {
            stream.writePOINT(point);
        }
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_LINE;
    }
}
