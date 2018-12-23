package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.DisplayDevice;
import com.neaterbits.displayserver.framebuffer.common.Encoder;
import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;
import com.neaterbits.displayserver.framebuffer.common.RenderingProvider;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Size;

abstract class XWindowsRenderingProvider extends XWindowsFrameBuffer implements RenderingProvider {

    private final XWindowsDriverConnection driverConnection;
    private final WINDOW window;
    
    XWindowsRenderingProvider(XWindowsDriverConnection driverConnection, int screen, WINDOW window, Size size, int depth) throws IOException {
        super(driverConnection, screen, window, size, depth);
        
        this.driverConnection = driverConnection;
        this.window = window;
    }

    @Override
    public final FrameBuffer getFrameBuffer() {
        return this;
    }

    @Override
    public OffscreenBuffer allocateOffscreenBuffer(Size size, PixelFormat pixelFormat) {
        try {
            return new XWindowsOffscreenBuffer(driverConnection, getScreenNo(), window, size, pixelFormat.getDepth());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void freeOffscreenBuffer(OffscreenBuffer buffer) {

        ((XWindowsOffscreenBuffer)buffer).dispose();
        
    }

    @Override
    public boolean supports(Encoder encoder) {
        return true;
    }

    @Override
    public boolean supports(DisplayDevice device) {
        return true;
    }
}
