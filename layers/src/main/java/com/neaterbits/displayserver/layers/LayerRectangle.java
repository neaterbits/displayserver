package com.neaterbits.displayserver.layers;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class LayerRectangle {
	
	private final int left;
	private final int top;
	private final int width;
	private final int height;
	
	LayerRectangle(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public LayerRectangle(Position position, Size size) {
		this(position.getLeft(), position.getTop(), size.getWidth(), size.getHeight());
	}
	
	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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
	
	
	public Intersection splitFromIntersectingButNotIn(LayerRectangle intersectsWith, List<LayerRectangle> list) {
		
		final Intersection intersection;
		
		if (this.obscurs(intersectsWith)) {
			intersection = Intersection.OBSCURING;
		}
		else if (intersectsWith.obscurs(this)) {
			intersection = Intersection.OBSCURED_BY;
		}
		else if (this.intersects(intersectsWith)) {
			intersection = Intersection.OVERLAP;
		}
		else {
			intersection = Intersection.NONE;
		}

		return intersection;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + left;
        result = prime * result + top;
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
        LayerRectangle other = (LayerRectangle) obj;
        if (height != other.height)
            return false;
        if (left != other.left)
            return false;
        if (top != other.top)
            return false;
        if (width != other.width)
            return false;
        return true;
    }
}