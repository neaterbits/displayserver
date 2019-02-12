package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;

public final class POINT extends XEncodeable {

    private final short x;
    private final short y;

    public static POINT decode(XWindowsProtocolInputStream stream) throws IOException {
        
        return new POINT(
                stream.readINT16(),
                stream.readINT16()
        );
    }
    
    public POINT(INT16 x, INT16 y) {
        this(x.getValue(), y.getValue());
    }

    public POINT(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writePOINT(this);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
