package com.neaterbits.displayserver.protocol.types;

public final class DRAWABLE extends RESOURCE {

	public DRAWABLE(int value) {
		super(value);
	}
	
	public WINDOW toWindow() {
	    return new WINDOW(this);
	}

	public PIXMAP toPixmap() {
	    return new PIXMAP(this);
	}
}
