package com.neaterbits.displayserver.layers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Region {

	private final List<Rectangle> rectangles;

	public Region(List<Rectangle> rectangles) {
		
		Objects.requireNonNull(rectangles);
		
		this.rectangles = Collections.unmodifiableList(rectangles);
	}

	public List<Rectangle> getRectangles() {
		return rectangles;
	}
}
