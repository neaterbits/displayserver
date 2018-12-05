package com.neaterbits.displayserver.protocol.types;

public final class WINDOW extends RESOURCE {

	public WINDOW(int value) {
		super(value);
	}
	
	public DRAWABLE toDrawable() {
		return new DRAWABLE(getValue());
	}
}
