package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.POINT;

public abstract class PolyPointRequest extends Request {


    public interface CreatePolyPointRequest<T extends PolyPointRequest> {

        T create(
                BYTE coordinateMode, 
                DRAWABLE drawable,
                GCONTEXT gc,
                POINT [] points);

    }
    
    
    private final BYTE coordinateMode;
    private final DRAWABLE drawable;
    private final GCONTEXT gc;
    private final POINT [] points;
    
    public static <T extends PolyPointRequest> T decode(XWindowsProtocolInputStream stream, CreatePolyPointRequest<T> createPolyPointRequest) throws IOException {
        
        final BYTE coordinateMode = stream.readBYTE();
        
        final CARD16 requestLength = stream.readCARD16();
        
        final int numPoints = requestLength.getValue() - 3;
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        
        final POINT [] points = new POINT[numPoints];
        
        for (int i = 0; i < numPoints; ++ i) {
            points[i] = stream.readPOINT();
        }
        
        return createPolyPointRequest.create(coordinateMode, drawable, gc, points);
    }
    
    public PolyPointRequest(BYTE coordinateMode, DRAWABLE drawable, GCONTEXT gc, POINT[] points) {

        Objects.requireNonNull(coordinateMode);
        Objects.requireNonNull(drawable);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(points);
        
        this.coordinateMode = coordinateMode;
        this.drawable = drawable;
        this.gc = gc;
        this.points = points;
    }

    public final BYTE getCoordinateMode() {
        return coordinateMode;
    }

    public final DRAWABLE getDrawable() {
        return drawable;
    }

    public final GCONTEXT getGC() {
        return gc;
    }

    public final POINT[] getPoints() {
        return points;
    }

    @Override
    public final Object[] getDebugParams() {
        return wrap(
                "coordinateMode", coordinateMode,
                "drawable", drawable,
                "gc", gc,
                "points", Arrays.toString(points)
                
        );
    }

    @Override
    public final void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(coordinateMode);
        
        writeRequestLength(stream, 3 + points.length);
        
        stream.writeDRAWABLE(drawable);
        stream.writeGCONTEXT(gc);

        encodeArray(points, stream);
    }
}
