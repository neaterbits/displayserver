package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class QueryColors extends Request {

    private final COLORMAP cmap;
    private final CARD32 [] pixels;

    public static QueryColors decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 requestLength = readRequestLength(stream);
        
        final COLORMAP cmap = stream.readCOLORMAP();
        
        final int numPixels = requestLength.getValue() - 2;
        
        final CARD32 [] pixels = new CARD32[numPixels];
        
        for (int i = 0; i < numPixels; ++ i) {
            pixels[i] = stream.readCARD32();
        }
        
        return new QueryColors(cmap, pixels);
    }
    
    public QueryColors(COLORMAP cmap, CARD32[] pixels) {
        
        Objects.requireNonNull(cmap);
        Objects.requireNonNull(pixels);
        
        this.cmap = cmap;
        this.pixels = pixels;
    }

    public COLORMAP getCmap() {
        return cmap;
    }

    public CARD32[] getPixels() {
        return pixels;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "cmap", cmap,
                "pixels", Arrays.toString(pixels)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2 + pixels.length);
        
        stream.writeCOLORMAP(cmap);
        
        for (CARD32 pixel : pixels) {
            stream.writeCARD32(pixel);
        }
    }

    @Override
    public int getOpCode() {
        return OpCodes.QUERY_COLORS;
    }
}
