package com.neaterbits.displayserver.types;

public final class Position {
	
	private final int left;
	private final int top;
	
	public Position(int left, int top) {
		this.left = left;
		this.top = top;
	}

	public int getLeft() {
		return left;
	}
	
	public int getTop() {
		return top;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + left;
        result = prime * result + top;
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
        Position other = (Position) obj;
        if (left != other.left)
            return false;
        if (top != other.top)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + top + ")";
    }
}
