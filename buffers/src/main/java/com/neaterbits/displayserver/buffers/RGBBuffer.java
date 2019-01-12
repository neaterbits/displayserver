package com.neaterbits.displayserver.buffers;

public final class RGBBuffer extends InMemoryBuffer {

	public RGBBuffer(int width, int height) {
		super(width, height, PixelFormat.RGB24);
	}

	@Override
	int getPixelRGBA(int pixelIdx) {
		return buffer[pixelIdx + 0] << 24
		     | buffer[pixelIdx + 1] << 16
		     | buffer[pixelIdx + 2] << 8;
	}

	@Override
	void setPixelRGBA(int pixelIdx, int rgba) {
		
		final int r = rgba >> 24;
		final int g = (rgba >> 16) & 0xFF;
		final int b = (rgba >> 8)  & 0xFF;
	
		buffer[pixelIdx + 0] = (byte)r;
		buffer[pixelIdx + 1] = (byte)g;
		buffer[pixelIdx + 2] = (byte)b;
	}

	@Override
	public PixelFormat getPixelFormat() {
		return PixelFormat.RGB24;
	}

    @Override
    public void putImage(int x, int y, int width, int height, PixelFormat format, byte[] data) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void getImage(int x, int y, int width, int height, PixelFormat format, GetImageListener listener) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void copyArea(BufferOperations src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public void flush() {
        
    }

    @Override
    public void writeTestImage(int x, int y, int width, int height) {
        
    }
}
