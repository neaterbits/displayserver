package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;

public final class SEGMENT extends XEncodeable {

    private final INT16 x1;
    private final INT16 y1;
    private final INT16 x2;
    private final INT16 y2;

    public static SEGMENT decode(XWindowsProtocolInputStream stream) throws IOException {
        return new SEGMENT(
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16());
    }
    
    public SEGMENT(INT16 x1, INT16 y1, INT16 x2, INT16 y2) {

        Objects.requireNonNull(x1);
        Objects.requireNonNull(y1);
        Objects.requireNonNull(x2);
        Objects.requireNonNull(y2);
        
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public INT16 getX1() {
        return x1;
    }

    public INT16 getY1() {
        return y1;
    }

    public INT16 getX2() {
        return x2;
    }

    public INT16 getY2() {
        return y2;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "x1", x1,
                "y1", y1,
                "x2", x2,
                "y2", y2
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeINT16(x1);
        stream.writeINT16(y1);
        stream.writeINT16(x2);
        stream.writeINT16(y2);
    }

    @Override
    public String toString() {
        return "(" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ")";
    }
}
