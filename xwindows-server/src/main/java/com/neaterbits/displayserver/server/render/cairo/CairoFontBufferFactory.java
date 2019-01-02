package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.CairoFormat;
import com.neaterbits.displayserver.render.cairo.CairoImageSurface;
import com.neaterbits.displayserver.xwindows.fonts.model.DataLength;
import com.neaterbits.displayserver.xwindows.fonts.model.FontBitmapFormat;
import com.neaterbits.displayserver.xwindows.fonts.model.StoreOrder;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.util.Bitmaps;

public final class CairoFontBufferFactory implements FontBufferFactory {
    
    private static final boolean DEBUG = false;
    
    private final StoreOrder nativeOrder;
    
    public CairoFontBufferFactory(StoreOrder nativeOrder) {

        Objects.requireNonNull(nativeOrder);
        
        this.nativeOrder = nativeOrder;
    }
    
    @Override
    public FontBuffer createFontBuffer(int glyphIndex, byte [] bitmap, FontBitmapFormat sourceBitmapFormat, int width, int height) {

        if (width <= 0) {
            throw new IllegalArgumentException();
        }
        
        if (height <= 0) {
            throw new IllegalArgumentException();
        }
        
        final CairoFormat format = CairoFormat.A1;

        final int stride = format.strideForWidth(width);
        
        if (stride <= 0) {
            throw new IllegalStateException();
        }

        final DataLength padding = DataLength.fromStride(stride);
        
        final FontBitmapFormat fontBitmapFormat = new FontBitmapFormat(
                nativeOrder,
                nativeOrder,
                padding,
                DataLength.INT);
        
        final byte [] data = new byte[stride * height];

        if (DEBUG) {
            System.out.println("## converting from " + sourceBitmapFormat + " to " + fontBitmapFormat);
        }

        FontBitmapFormat.convert(sourceBitmapFormat, bitmap, fontBitmapFormat, data, width, height);
        
        if (DEBUG) {
            if (glyphIndex >= 34 && glyphIndex <= 37) {
                
                System.out.println("## converted data:\n");
    
                sourceBitmapFormat.printFontBitmap(System.out, bitmap, width, height);
                
                //FontBitmapFormat.DEBUG = true;
                fontBitmapFormat.printFontBitmap(System.out, data, width, height);
                //FontBitmapFormat.DEBUG = false;
                
                Bitmaps.printBitmap(System.out, data, stride);
            }
        }
        
        // Arrays.fill(data, (byte)0x33);
        
        final CairoImageSurface surface = new CairoImageSurface(data, format, width, height, stride);
        
        return new CairoFontBuffer(surface);
    }
}
