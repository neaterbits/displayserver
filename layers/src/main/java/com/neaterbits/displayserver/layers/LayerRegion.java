package com.neaterbits.displayserver.layers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LayerRegion {

	private final List<LayerRectangle> rectangles;

	public LayerRegion(List<LayerRectangle> rectangles) {
		
		Objects.requireNonNull(rectangles);
		
		this.rectangles = Collections.unmodifiableList(rectangles);
	}

	public List<LayerRectangle> getRectangles() {
		return rectangles;
	}
}
