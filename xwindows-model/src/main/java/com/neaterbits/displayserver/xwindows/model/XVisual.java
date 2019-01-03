package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.enums.VisualClass;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.VISUALTYPE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class XVisual {

    private final int _class;
    private final int bitsPerRGBValue;
    private final int colormapEntries;
    private final int redMask;
    private final int greenMask;
    private final int blueMask;
    
    public XVisual(int _class, int bitsPerRGBValue, int colormapEntries, int redMask, int greenMask, int blueMask) {
        this._class = _class;
        this.bitsPerRGBValue = bitsPerRGBValue;
        this.colormapEntries = colormapEntries;
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
    }

    public int getVisualClass() {
        return _class;
    }

    public int getBitsPerRGBValue() {
        return bitsPerRGBValue;
    }

    public int getColormapEntries() {
        return colormapEntries;
    }

    public int getRedMask() {
        return redMask;
    }

    public int getGreenMask() {
        return greenMask;
    }

    public int getBlueMask() {
        return blueMask;
    }

    VISUALTYPE toVISUALTYPE(VISUALID visualId) {
        
        final VISUALTYPE visualType = new VISUALTYPE(
                visualId,
                VisualClass.TrueColor,
                new CARD8((short)bitsPerRGBValue),
                new CARD16(colormapEntries),
                new CARD32(redMask),
                new CARD32(greenMask),
                new CARD32(blueMask));

        return visualType;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _class;
        result = prime * result + bitsPerRGBValue;
        result = prime * result + blueMask;
        result = prime * result + colormapEntries;
        result = prime * result + greenMask;
        result = prime * result + redMask;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        XVisual other = (XVisual) obj;
        if (_class != other._class)
            return false;
        if (bitsPerRGBValue != other.bitsPerRGBValue)
            return false;
        if (blueMask != other.blueMask)
            return false;
        if (colormapEntries != other.colormapEntries)
            return false;
        if (greenMask != other.greenMask)
            return false;
        if (redMask != other.redMask)
            return false;
        return true;
    }
}
