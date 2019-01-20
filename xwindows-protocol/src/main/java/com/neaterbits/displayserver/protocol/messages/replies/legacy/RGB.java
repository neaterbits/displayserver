package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class RGB extends XEncodeable {

    private final CARD16 red;
    private final CARD16 green;
    private final CARD16 blue;

    public RGB(CARD16 red, CARD16 green, CARD16 blue) {
        
        Objects.requireNonNull(red);
        Objects.requireNonNull(green);
        Objects.requireNonNull(blue);
        
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public CARD16 getRed() {
        return red;
    }

    public CARD16 getGreen() {
        return green;
    }

    public CARD16 getBlue() {
        return blue;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "red", red,
                "green", green,
                "blue", blue
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD16(red);
        stream.writeCARD16(green);
        stream.writeCARD16(blue);
        
        stream.writeCARD16(new CARD16(0));
    }
}
