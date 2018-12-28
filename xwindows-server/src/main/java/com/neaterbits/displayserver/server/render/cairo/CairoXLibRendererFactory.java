package com.neaterbits.displayserver.server.render.cairo;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;

public final class CairoXLibRendererFactory implements XLibRendererFactory {

    @Override
    public XLibRenderer createRenderer(BufferOperations bufferOperations) {
        return new CairoXLibRenderer(bufferOperations.createCairoSurface());
    }

}
