package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.POINT;

public abstract class PolyPointRequest extends PolyRequest<POINT> {

    private static final int CARD32_PER_ENTRY = 1;
    
    @FunctionalInterface
    public interface CreatePolyPointRequest<REQUEST extends PolyPointRequest> {

        REQUEST create(
                BYTE coordinateMode, 
                DRAWABLE drawable,
                GCONTEXT gc,
                POINT [] points);

    }
    
    private final BYTE coordinateMode;
    
    public static <REQUEST extends PolyPointRequest> REQUEST decode(XWindowsProtocolInputStream stream, CreatePolyPointRequest<REQUEST> createPolyPointRequest) throws IOException {
        
        return PolyRequest.decode(
                stream,
                CARD32_PER_ENTRY,
                POINT[]::new,
                POINT::decode,
                (BYTE initialByte, DRAWABLE drawable, GCONTEXT gc, POINT [] list) -> createPolyPointRequest.create(initialByte, drawable, gc, list));
    }
    
    
    public PolyPointRequest(BYTE coordinateMode, DRAWABLE drawable, GCONTEXT gc, POINT[] points) {

        super(drawable, gc, points);
        
        Objects.requireNonNull(coordinateMode);
        
        this.coordinateMode = coordinateMode;
    }

    public final BYTE getCoordinateMode() {
        return coordinateMode;
    }

    public final POINT[] getPoints() {
        return getList();
    }

    @Override
    public final Object[] getDebugParams() {
        return wrap(
                "coordinateMode", coordinateMode,
                "drawable", getDrawable(),
                "gc", getGC(),
                "points", getListDebugParam()
                
        );
    }

    @Override
    public final void encode(XWindowsProtocolOutputStream stream) throws IOException {
        super.encode(stream, coordinateMode, CARD32_PER_ENTRY);
    }
}
