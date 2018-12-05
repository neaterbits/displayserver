package com.neaterbits.displayserver.protocol.types;

public final class WINDOW extends RESOURCE {

    public static final WINDOW None = new WINDOW(0);
    
	public WINDOW(int value) {
		super(value);
	}
	
	public DRAWABLE toDrawable() {
		return new DRAWABLE(getValue());
	}
}
