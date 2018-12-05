package com.neaterbits.displayserver.protocol.types;

public final class PIXMAP extends RESOURCE {

	public PIXMAP(int value) {
		super(value);
	}

	public DRAWABLE toDrawable() {
		return new DRAWABLE(getValue());
	}
}
