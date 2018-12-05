package com.neaterbits.displayserver.buffers;

public interface BufferInfo {

	int getWidth();
	
	int getHeight();
	
	int getBytesPerPixel();
	
	PixelFormat getPixelFormat();
}
