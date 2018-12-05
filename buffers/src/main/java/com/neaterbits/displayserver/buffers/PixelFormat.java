package com.neaterbits.displayserver.buffers;

public enum PixelFormat {

	RGBA32(24, 32, 4, 8, 1 << 24, 0xFF000000, 0x00FF0000, 0x0000FF00),
	
	RGB24(24, 24, 3, 8, 1 << 24, 0x00FF0000, 0x0000FF00, 0x000000FF);
	
	private final int depth;
	private final int bitsPerPixel;
	private final int bytesPerPixel;
	
	private final int bitsPerColorComponent;
	private final int numberOfDistinctColors;
	
	private final int redMask;
	private final int greenMask;
	private final int blueMask;
	
	
    private PixelFormat(int depth, int bitsPerPixel, int bytesPerPixel,
            int bitsPerColorComponent, int numberOfDistinctColors,
            int redMask, int greenMask, int blueMask) {
        
        this.depth = depth;
        this.bitsPerPixel = bitsPerPixel;
        this.bytesPerPixel = bytesPerPixel;
    
        this.bitsPerColorComponent = bitsPerColorComponent;
        this.numberOfDistinctColors = numberOfDistinctColors;
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
    }

    public int getDepth() {
        return depth;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getBytesPerPixel() {
        return bytesPerPixel;
    }

    public int getBitsPerColorComponent() {
        return bitsPerColorComponent;
    }

    public int getNumberOfDistinctColors() {
        return numberOfDistinctColors;
    }

    public int getRedMask() {
        return redMask;
    }

    public int getGreenMask() {
        return greenMask;
    }

    public int getBlueMask() {
        return blueMask;
    }
}
