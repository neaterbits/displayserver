package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class FreeColors extends XRequest {

    private final COLORMAP cmap;
    private final CARD32 planeMask;
    private final CARD32 [] pixels;
    
    public static FreeColors decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        final CARD16 requestLength = readRequestLength(stream);
        
        final int numPixels = requestLength.getValue() - 3;
        
        return new FreeColors(
                stream.readCOLORMAP(),
                stream.readCARD32(),
                decodeArray(stream, new CARD32[numPixels], XWindowsProtocolInputStream::readCARD32));
    }
    
    public FreeColors(COLORMAP cmap, CARD32 planeMask, CARD32[] pixels) {

        Objects.requireNonNull(cmap);
        Objects.requireNonNull(planeMask);
        Objects.requireNonNull(pixels);
        
        this.cmap = cmap;
        this.planeMask = planeMask;
        this.pixels = pixels;
    }

    public COLORMAP getCmap() {
        return cmap;
    }

    public CARD32 getPlaneMask() {
        return planeMask;
    }

    public CARD32[] getPixels() {
        return pixels;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "cmap", cmap,
                "planeMask", planeMask,
                "pixels", Arrays.stream(pixels).map(pixel -> String.format("%08x", pixel.getValue())).collect(Collectors.toList())
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3 + pixels.length);
        
        stream.writeCOLORMAP(cmap);
    
        stream.writeCARD32(planeMask);
    
        encodeArray(pixels, stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.FREE_COLORS;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
