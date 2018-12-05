package com.neaterbits.displayserver.protocol;

public class OpCodes {

	public static final int CREATE_WINDOW = 1;

	public static final int DESTROY_WINDOW = 4;
	
	public static final int MAP_WINDOW = 8;

	public static final int GET_PROPERTY = 20;
	
	public static final int CREATE_PIXMAP = 53;
    public static final int FREE_PIXMAP = 54;
    public static final int CREATE_GC = 55;
	
	public static final int COPY_AREA = 62;
	
	public static final int PUT_IMAGE = 72;
	
	public static final int ALLOC_COLOR = 84;
	
	public static final int QUERY_EXTENSION = 98;
}
