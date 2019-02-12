package com.neaterbits.displayserver.layers;

public abstract class LayerRectangleBase {

    final int left;
    final int top;
    final int width;
    final int height;

    LayerRectangleBase(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    LayerRectangleBase(LayerRectangleBase toCopy) {

        this.left = toCopy.left;
        this.top = toCopy.top;
        this.width = toCopy.width;
        this.height = toCopy.height;
    }

    public final int getLeft() {
        return left;
    }

    public final int getTop() {
        return top;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    final int getRight() {
        return left + width - 1;
    }
    
    final int getLower() {
        return top + height - 1;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + left;
        result = prime * result + top;
        result = prime * result + width;
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
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

    @Override
    public final String toString() {
        return "(" + left + ", " + top + ", " + width + ", " + height + ")";
    }
}
