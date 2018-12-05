package com.neaterbits.displayserver.windows;

public final class WindowParameters {

	private final WindowClass windowClass;
	private final int depth;
	private final Visual visual;

	private final int x;
	private final int y;

	private final int width;
	private final int height;
	
	private final int borderWidth;

	public WindowParameters(
			WindowClass windowClass,
			int depth, Visual visual,
			int x, int y, int width, int height,
			int borderWidth) {
		this.windowClass = windowClass;
		this.depth = depth;
		this.visual = visual;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.borderWidth = borderWidth;
	}

	public WindowClass getWindowClass() {
		return windowClass;
	}

	public int getDepth() {
		return depth;
	}

	public Visual getVisual() {
		return visual;
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

	public int getBorderWidth() {
		return borderWidth;
	}
}
