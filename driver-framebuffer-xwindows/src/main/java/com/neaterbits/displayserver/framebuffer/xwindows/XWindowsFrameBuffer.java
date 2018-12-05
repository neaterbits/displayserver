package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.BufferUpdate;
import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;
import com.neaterbits.displayserver.protocol.messages.requests.DestroyWindow;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Size;

final class XWindowsFrameBuffer implements FrameBuffer {

	private final XWindowsDriverConnection driverConnection;
	private final WINDOW window;
	private final GCONTEXT gc;
	
	private final Size size;
	private final int depth;
	
	public XWindowsFrameBuffer(XWindowsDriverConnection driverConnection, WINDOW window, GCONTEXT gc, Size size, int depth) {
		
		Objects.requireNonNull(driverConnection);
		Objects.requireNonNull(window);
		Objects.requireNonNull(gc);
		
		this.driverConnection = driverConnection;
		this.window = window;
		this.gc = gc;

		this.size = size;
		this.depth = depth;
	}

	@Override
    public Size getSize() {
        return size;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
	public void updateAreas(BufferUpdate[] updates) {

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
        
        if (update.getPixelFormat().getDepth() != depth) {
            throw new IllegalArgumentException();
        }
        
        int scanlinesLeftToWrite = update.getHeight();
        
        final int dstX = update.getX();
        
        int dstY = update.getY();
        
        int dataOffset = 0;
        
        final int maxBytes = (65536 - 6) * 4;
        
        final int stride = update.getWidth() * update.getPixelFormat().getBytesPerPixel();
        
        if (stride > maxBytes) {
            throw new UnsupportedOperationException();
        }

        final int numLines = maxBytes % stride;
        
        while (scanlinesLeftToWrite > 0) {
            
            final int height = scanlinesLeftToWrite > numLines
                    ? numLines
                    : scanlinesLeftToWrite;
            
            final int dataLength = height * stride;
            
            final PutImage putImage = new PutImage(
                    new BYTE((byte)2),
                    window.toDrawable(), gc,
                    new CARD16(update.getWidth()), new CARD16(height),
                    new INT16((short)dstX), new INT16((short)dstY),
                    new CARD8((short)0),
                    new CARD8((short)depth),
                    update.getData(),
                    dataOffset,
                    dataLength);
            
            driverConnection.sendRequest(putImage);
            
            scanlinesLeftToWrite -= height;
        }
    }
    
	@Override
	public void close() throws IOException {
		final DestroyWindow request = new DestroyWindow(window);
	
		driverConnection.sendRequest(request);
	}
}
