package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.ImageBufferFormat;
import com.neaterbits.displayserver.buffers.PixelConversion;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;

public final class CairoXLibRendererFactory implements XLibRendererFactory {

    @Override
    public XLibRenderer createRenderer(BufferOperations bufferOperations, PixelConversion pixelConversion) {
        
        Objects.requireNonNull(bufferOperations);
        Objects.requireNonNull(pixelConversion);
        
        return new CairoXLibRenderer(bufferOperations.createCairoSurface(), pixelConversion);
    }

    @Override
    public ImageBufferFormat getPreferedImageBufferFormat(int depth) {

        final ImageBufferFormat format;
        
        switch (depth) {
        
        case 24:
            // see CAIRO_FORMAT_RGB24
            format = new ImageBufferFormat(PixelFormat.RGB32, 32);
            break;
         
        default:
            throw new UnsupportedOperationException();
            
        }
        
        return format;
    }
}
