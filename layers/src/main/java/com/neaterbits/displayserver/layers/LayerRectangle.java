package com.neaterbits.displayserver.layers;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class LayerRectangle extends LayerRectangleIntersection {
	
	LayerRectangle(int left, int top, int width, int height) {
	    super(left, top, width, height);
	}
	
	public LayerRectangle(LayerRectangle toCopy) {
        super(toCopy);
    }

    public LayerRectangle(Position position, Size size) {
		this(position.getLeft(), position.getTop(), size.getWidth(), size.getHeight());
	}
	
	public boolean contains(int x, int y) {
		return x >= left && x < left + width && y >= top && y < top + height;
	}
	
	public boolean intersects(LayerRectangle other) {

		Objects.requireNonNull(other);
		
		return 
			   (left <= other.left               && left + width >  other.left)
			|| (left <  other.left + other.width && left + width >= other.left + other.width)
			|| (top  <= other.top                && top + height >  other.top)
			|| (top  <  other.top + other.height && top + height >= other.top + other.height);
		
	}
	
	public boolean obscurs(LayerRectangle other) {

		Objects.requireNonNull(other);
		
		return 
			   (left <= other.left               && left + width >=  other.left + other.width)
			&& (top  <= other.top                && top + height >   other.top  + other.height);
		
	}
	
	
	public OverlapType splitFromIntersectingButNotIn(LayerRectangle intersectsWith, List<LayerRectangle> list) {
		
		final OverlapType overlap;
		
		if (this.obscurs(intersectsWith)) {
			overlap = OverlapType.OBSCURING;
		}
		else if (intersectsWith.obscurs(this)) {
			overlap = OverlapType.OBSCURED_BY;
		}
		else if (this.intersects(intersectsWith)) {
			overlap = OverlapType.INTERSECTION;
		}
		else {
			overlap = OverlapType.NONE;
		}

		return overlap;
	}
}
