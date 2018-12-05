package com.neaterbits.displayserver.buffers;

public abstract class InMemoryBuffer extends ImageBuffer {

	final byte [] buffer;
	
	public InMemoryBuffer(int width, int height, int bytesPerPixel) {
		
		super(width, height, bytesPerPixel);
		
		this.buffer = new byte[width * height * bytesPerPixel];
	}
}
