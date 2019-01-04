package com.neaterbits.displayserver.xwindows.model.render;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.ImageBufferFormat;
import com.neaterbits.displayserver.buffers.PixelConversion;

public interface XLibRendererFactory {

    XLibRenderer createRenderer(BufferOperations bufferOperations, PixelConversion pixelConversion);

    ImageBufferFormat getPreferedImageBufferFormat(int depth);
    
}
