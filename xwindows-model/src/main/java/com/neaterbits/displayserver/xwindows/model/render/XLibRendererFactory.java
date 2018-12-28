package com.neaterbits.displayserver.xwindows.model.render;

import com.neaterbits.displayserver.buffers.BufferOperations;

public interface XLibRendererFactory {

    XLibRenderer createRenderer(BufferOperations bufferOperations);
}
