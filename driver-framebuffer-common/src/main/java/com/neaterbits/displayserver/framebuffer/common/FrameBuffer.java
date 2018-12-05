package com.neaterbits.displayserver.framebuffer.common;

import java.io.IOException;

import com.neaterbits.displayserver.types.Size;

public interface FrameBuffer {

    Size getSize();
    
    int getDepth();
    
	void updateAreas(BufferUpdate [] updates);

	void close() throws IOException;
}
