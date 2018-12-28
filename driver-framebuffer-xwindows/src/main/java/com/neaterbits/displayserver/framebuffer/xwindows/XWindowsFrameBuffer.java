package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.BufferUpdate;
import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Size;

class XWindowsFrameBuffer extends XWindowsBaseBuffer implements FrameBuffer {

    private final int screen;
	private final WINDOW window;
	
	XWindowsFrameBuffer(XWindowsDriverConnection driverConnection, int screen, WINDOW window, Size size, int depth) throws IOException {
	    super(driverConnection, XWindowsClientHelper.createGC(driverConnection, window), size, depth);
		
		Objects.requireNonNull(window);
		
		this.screen = screen;
		this.window = window;
	}

	@Override
    final DRAWABLE getDrawable() {
        return window.toDrawable();
    }

    @Override
    public final Size getFrameBufferSize() {
        return size;
    }

    @Override
    int getScreenNo() {
        return screen;
    }

    @Override
    public final int getDepth() {
        return depth;
    }
    
    @Override
    public final PixelFormat getPixelFormat() {
        return PixelFormat.RGB24;
    }

    @Override
	public final void updateAreas(BufferUpdate[] updates) {

        try {
            for (BufferUpdate update : updates) {
                updateArea(update);
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
	}

    private void updateArea(BufferUpdate update) throws IOException {

        putImage(
                update.getX(), update.getY(),
                update.getWidth(), update.getHeight(),
                update.getPixelFormat(),
                update.getData());
    }

    @Override
	public void close() throws IOException {
		final DestroyWindow request = new DestroyWindow(window);
	
		driverConnection.sendRequest(request);
	}
}
