package com.neaterbits.displayserver.types;

public final class Size {
	
	private final int width;
	private final int height;

	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean contains(int x, int y) {

	    if (x < 0) {
            throw new IllegalArgumentException();
        }
        
        if (y < 0) {
            throw new IllegalArgumentException();
        }

        return x < width && y < height;
	}
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Size other = (Size) obj;
        if (height != other.height)
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + width + ", " + height + ")";
    }
}
