package com.neaterbits.displayserver.protocol.types;

public final class COLORMAP extends RESOURCE {

    public static final COLORMAP None = new COLORMAP(0);

	public COLORMAP(int value) {
		super(value);
	}
}
