package com.neaterbits.displayserver.layers;

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
}
