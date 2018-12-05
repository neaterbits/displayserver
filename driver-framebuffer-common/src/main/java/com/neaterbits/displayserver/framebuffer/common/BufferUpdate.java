package com.neaterbits.displayserver.framebuffer.common;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;

public final class BufferUpdate {

	private final int x;
	private final int y;
	
	private final int width;
	private final int height;
	
	private final PixelFormat pixelFormat;
	
	private final byte [] data;

	public BufferUpdate(int x, int y, int width, int height, PixelFormat pixelFormat, byte[] data) {

		Objects.requireNonNull(pixelFormat);
		Objects.requireNonNull(data);
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pixelFormat = pixelFormat;
		this.data = data;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public PixelFormat getPixelFormat() {
		return pixelFormat;
	}

	public byte[] getData() {
		return data;
	}
}
