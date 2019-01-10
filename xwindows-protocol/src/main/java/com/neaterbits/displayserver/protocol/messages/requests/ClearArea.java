package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class ClearArea extends Request {

    private final BOOL exposures;
    
    private final WINDOW window;
    
    private final INT16 x;
    private final INT16 y;
    
    private final CARD16 width;
    private final CARD16 height;

    public static ClearArea decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL exposures = stream.readBOOL();
        
        readRequestLength(stream);
        
        return new ClearArea(
                exposures,
                stream.readWINDOW(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readCARD16(),
                stream.readCARD16());
    }
    
    public ClearArea(BOOL exposures, WINDOW window, INT16 x, INT16 y, CARD16 width, CARD16 height) {
    
        Objects.requireNonNull(exposures);
        Objects.requireNonNull(window);
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        
        this.exposures = exposures;
        this.window = window;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public BOOL getExposures() {
        return exposures;
    }

    public WINDOW getWindow() {
        return window;
    }

    public INT16 getX() {
        return x;
    }

    public INT16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "exposures", exposures,
                "window", window,
                "x", x,
                "y", y,
                "width", width,
                "height", height
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(exposures);
        
        writeRequestLength(stream, 4);
        
        stream.writeWINDOW(window);
        
        stream.writeINT16(x);
        stream.writeINT16(y);
        
        stream.writeCARD16(width);
        stream.writeCARD16(height);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CLEAR_AREA;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
