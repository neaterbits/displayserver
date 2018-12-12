package com.neaterbits.displayserver.buffers;

public abstract class InMemoryBuffer extends ImageBuffer {

	final byte [] buffer;
	
	public InMemoryBuffer(int width, int height, PixelFormat pixelFormat) {
		
		super(width, height, pixelFormat);
		
		this.buffer = new byte[width * height * getBytesPerPixel()];
	}
}
