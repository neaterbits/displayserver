package com.neaterbits.displayserver.protocol.types;

public final class PIXMAP extends RESOURCE {

    public static final PIXMAP None = new PIXMAP(0);

	public PIXMAP(int value) {
		super(value);
	}

	PIXMAP(DRAWABLE drawable) {
	    this(drawable.getValue());
	}
	
	public DRAWABLE toDrawable() {
		return new DRAWABLE(getValue());
	}
}
