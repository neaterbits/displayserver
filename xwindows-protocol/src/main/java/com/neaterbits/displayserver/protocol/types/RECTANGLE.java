package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;

public final class RECTANGLE extends Encodeable {

    private final INT16 x;
    private final INT16 y;
    
    private final CARD16 width;
    private final CARD16 height;

    public static RECTANGLE decode(XWindowsProtocolInputStream stream) throws IOException {
        return new RECTANGLE(
                stream.readINT16(),
                stream.readINT16(),
                stream.readCARD16(),
                stream.readCARD16());
    }
    
    public RECTANGLE(INT16 x, INT16 y, CARD16 width, CARD16 height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
                "x", x,
                "y", y,
                "width", width,
                "height", height
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeINT16(x);
        stream.writeINT16(y);
        stream.writeCARD16(width);
        stream.writeCARD16(height);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + width + ", " + height + ")";
    }
}
