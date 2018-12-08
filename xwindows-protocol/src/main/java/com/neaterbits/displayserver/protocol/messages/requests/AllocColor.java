package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class AllocColor extends Request {

    private final COLORMAP cmap;
    
    private final CARD16 red;
    private final CARD16 green;
    private final CARD16 blue;

    public static AllocColor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        stream.readBYTE();
        
        stream.readCARD16();
        
        final AllocColor allocColor = new AllocColor(
                stream.readCOLORMAP(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16());
        
        stream.readCARD16();
        
        return allocColor;
    }
    
    public AllocColor(COLORMAP cmap, CARD16 red, CARD16 green, CARD16 blue) {
        this.cmap = cmap;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public COLORMAP getCmap() {
        return cmap;
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
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        stream.writeBYTE(new BYTE((byte)0));
        
        stream.writeCARD16(new CARD16(4));
        stream.writeCOLORMAP(cmap);
        
        stream.writeCARD16(red);
        stream.writeCARD16(green);
        stream.writeCARD16(blue);
        
        stream.writeCARD16(new CARD16(0));
    }

    @Override
    public int getOpCode() {
        return OpCodes.ALLOC_COLOR;
    }
}
