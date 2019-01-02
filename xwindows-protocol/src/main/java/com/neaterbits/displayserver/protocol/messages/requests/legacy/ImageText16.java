package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.STRING16;

public final class ImageText16 extends Request {

    private final DRAWABLE drawable;
    private final GCONTEXT gc;
    private final INT16 x;
    private final INT16 y;
    private final STRING16 string;
    
    public static ImageText16 decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE stringLength = stream.readBYTE();
        
        readRequestLength(stream);
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        final INT16 x = stream.readINT16();
        final INT16 y = stream.readINT16();
        final STRING16 string = stream.readSTRING16(stringLength.getValue());
        
        final int padding = XWindowsProtocolUtil.getPadding(stringLength.getValue() * 2);
        
        stream.readPad(padding);
        
        return new ImageText16(drawable, gc, x, y, string);
    }
    
    public ImageText16(DRAWABLE drawable, GCONTEXT gc, INT16 x, INT16 y, STRING16 string) {

        Objects.requireNonNull(drawable);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        Objects.requireNonNull(string);
        
        this.drawable = drawable;
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.string = string;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public INT16 getX() {
        return x;
    }

    public INT16 getY() {
        return y;
    }

    public STRING16 getString() {
        return string;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                
                "drawable", drawable,
                "gc", gc,
                "x", x,
                "y", y,
                "string", string
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        final int stringLength = string.length();
        
        if (stringLength > 0xFF) {
            throw new IllegalArgumentException();
        }
        
        stream.writeBYTE(new BYTE((byte)stringLength));
        
        final int stringLengthBytes = 2 * stringLength;
        
        final int padding = XWindowsProtocolUtil.getPadding(stringLengthBytes);
        
        writeRequestLength(stream, 4 + (stringLengthBytes + padding) / 4);
        
        stream.writeDRAWABLE(drawable);
        stream.writeGCONTEXT(gc);
        stream.writeINT16(x);
        stream.writeINT16(y);
        stream.writeSTRING16(string);
        stream.pad(padding);
    }


    @Override
    public int getOpCode() {
        return OpCodes.IMAGE_TEXT_16;
    }
}
