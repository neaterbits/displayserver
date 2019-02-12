package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.POINT;

public final class FillPoly extends DrawRequest {

    private final BYTE shape;
    private final BYTE coordinateMode;
    private final POINT [] points;

    public static FillPoly decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 requestLength = readRequestLength(stream);
        
        final int numPoints = requestLength.getValue() - 4;
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        
        final BYTE shape = stream.readBYTE();
        final BYTE coordinateMode = stream.readBYTE();
        
        readUnusedCARD16(stream);
        
        return new FillPoly(
                drawable,
                gc,
                shape,
                coordinateMode,
                decodeArray(stream, new POINT[numPoints], POINT::decode));
        
    }
    
    public FillPoly(DRAWABLE drawable, GCONTEXT gc, BYTE shape, BYTE coordinateMode, POINT[] points) {
        super(drawable, gc);
        
        this.shape = shape;
        this.coordinateMode = coordinateMode;
        this.points = points;
    }

    public BYTE getShape() {
        return shape;
    }

    public BYTE getCoordinateMode() {
        return coordinateMode;
    }

    public POINT[] getPoints() {
        return points;
    }

    
    
    @Override
    public Object[] getDebugParams() {
        return wrap(

                "drawable", getDrawable(),
                "gc", getGC(),
                "shape", shape,
                "coordinateMode", coordinateMode,
                "points", Arrays.toString(points)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 4 + points.length);
        
        stream.writeDRAWABLE(getDrawable());
        stream.writeGCONTEXT(getGC());
        
        stream.writeBYTE(shape);
        stream.writeBYTE(coordinateMode);
        
        writeUnusedCARD16(stream);
        
        encodeArray(points, stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.FILL_POLY;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
