package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class VISUALTYPE extends Encodeable {
    
    private final VISUALID visualId;
    private final BYTE _class;
    private final CARD8 bitsPerRGBValue;
    private final CARD16 colormapEntries;
    private final CARD32 redMask;
    private final CARD32 greenMask;
    private final CARD32 blueMask;
    
    public static VISUALTYPE decode(XWindowsProtocolInputStream stream) throws IOException {
    
        final VISUALTYPE visualtype = new VISUALTYPE(
                stream.readVISUALID(),
                stream.readBYTE(),
                stream.readCARD8(),
                stream.readCARD16(),
                
                stream.readCARD32(),
                stream.readCARD32(),
                stream.readCARD32());

        stream.readCARD32();
        
        return visualtype;
    }
    
    public VISUALTYPE(
            VISUALID visualId,
            BYTE _class,
            CARD8 bitsPerRGBValue,
            CARD16 colormapEntries,
            CARD32 redMask,
            CARD32 greenMask,
            CARD32 blueMask) {
        this.visualId = visualId;
        this._class = _class;
        this.bitsPerRGBValue = bitsPerRGBValue;
        this.colormapEntries = colormapEntries;
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
    }

    public VISUALID getVisualId() {
        return visualId;
    }

    public BYTE getVisualclass() {
        return _class;
    }

    public CARD8 getBitsPerRGBValue() {
        return bitsPerRGBValue;
    }

    public CARD16 getColormapEntries() {
        return colormapEntries;
    }

    public CARD32 getRedMask() {
        return redMask;
    }

    public CARD32 getGreenMask() {
        return greenMask;
    }

    public CARD32 getBlueMask() {
        return blueMask;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        stream.writeVISUALID(visualId);
        stream.writeBYTE(_class);
        stream.writeCARD8(bitsPerRGBValue);
        stream.writeCARD16(colormapEntries);
        
        stream.writeCARD32(redMask);
        stream.writeCARD32(greenMask);
        stream.writeCARD32(blueMask);
        
        stream.writeCARD32(new CARD32(0));
    }

    @Override
    public String toString() {
        return "VISUALTYPE [visualId=" + visualId + ", _class=" + _class + ", bitsPerRGBValue=" + bitsPerRGBValue
                + ", colormapEntries=" + colormapEntries + ", redMask=" + redMask + ", greenMask=" + greenMask
                + ", blueMask=" + blueMask + "]";
    }
}
