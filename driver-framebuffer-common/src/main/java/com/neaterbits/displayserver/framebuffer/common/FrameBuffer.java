package com.neaterbits.displayserver.framebuffer.common;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.types.Size;

public interface FrameBuffer extends DisplayPlane {

    Size getFrameBufferSize();
    
    int getDepth();
    
    PixelFormat getPixelFormat();
    
	void updateAreas(BufferUpdate [] updates);

	void close() throws IOException;
}
