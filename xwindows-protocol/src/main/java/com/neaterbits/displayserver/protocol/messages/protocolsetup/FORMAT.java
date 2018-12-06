package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class FORMAT extends Encodeable {

    private final CARD8 depth;
    private final CARD8 bitsPerPixel;
    private final CARD8 scanlinePad;
    
    public static FORMAT decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final FORMAT format = new FORMAT(stream.readCARD8(), stream.readCARD8(), stream.readCARD8());
    
        stream.readPad(5);

        return format;
    }
    
    public FORMAT(CARD8 depth, CARD8 bitsPerPixel, CARD8 scanlinePad) {
        this.depth = depth;
        this.bitsPerPixel = bitsPerPixel;
        this.scanlinePad = scanlinePad;
    }

    public CARD8 getDepth() {
        return depth;
    }
    
    public CARD8 getBitsPerPixel() {
        return bitsPerPixel;
    }

    public CARD8 getScanlinePad() {
        return scanlinePad;
    }
    
    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD8(depth);
        stream.writeCARD8(bitsPerPixel);
        stream.writeCARD8(scanlinePad);
        
        stream.pad(5);
    }

    @Override
    public String toString() {
        return "FORMAT [depth=" + depth + ", bitsPerPixel=" + bitsPerPixel + ", scanlinePad=" + scanlinePad + "]";
    }
}
