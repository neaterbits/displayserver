package com.neaterbits.displayserver.protocol.enums;

public class OpCodes {

	public static final int CREATE_WINDOW = 1;
	public static final int CHANGE_WINDOW_ATTRIBUTES = 2;
	public static final int GET_WINDOW_ATTRIBUTES = 3;

	public static final int DESTROY_WINDOW = 4;
	
	public static final int MAP_WINDOW = 8;
	public static final int MAP_SUBWINDOWS = 9;
	public static final int UNMAP_WINDOW = 10;

	public static final int CONFIGURE_WINDOW = 12;
	
	public static final int GET_GEOMETRY = 14;
	
	public static final int QUERY_TREE = 15;
	public static final int INTERN_ATOM = 16;
	
	public static final int CHANGE_PROPERTY = 18;
	public static final int DELETE_PROPERTY = 19;
	public static final int GET_PROPERTY = 20;
	public static final int LIST_PROPERTIES = 21;
	
	public static final int GET_SELECTION_OWNER = 23;
	public static final int CONVERT_SELECTION = 24;
	
	public static final int GRAB_BUTTON = 28;
	
	public static final int GRAB_SERVER = 36;
    public static final int UNGRAB_SERVER = 37;
    public static final int QUERY_POINTER = 38;
	
    public static final int SET_INPUT_FOCUS = 42;
    public static final int GET_INPUT_FOCUS = 43;
    
    public static final int OPEN_FONT = 45;
    public static final int CLOSE_FONT = 46;
    public static final int QUERY_FONT = 47;

	public static final int CREATE_PIXMAP = 53;
    public static final int FREE_PIXMAP = 54;
    public static final int CREATE_GC = 55;
    public static final int CHANGE_GC = 56;
    
    public static final int FREE_GC = 60;
	
    public static final int CLEAR_AREA = 61;
	public static final int COPY_AREA = 62;
	
	public static final int POLY_POINT = 64;
	public static final int POLY_LINE = 65;
	public static final int POLY_SEGMENT = 66;
	public static final int POLY_FILL_RECTANGLE = 70;
	
	public static final int PUT_IMAGE = 72;
	public static final int GET_IMAGE = 73;
	
	public static final int IMAGE_TEXT_16 = 77;
	
	public static final int CREATE_COLOR_MAP = 78;
	
	public static final int ALLOC_COLOR = 84;
	
	public static final int FREE_COLORS = 88;
	
	public static final int QUERY_COLORS = 91;
	public static final int LOOKUP_COLOR = 92;
	public static final int CREATE_CURSOR = 93;
	public static final int CREATE_GLYPH_CURSOR = 94;
	public static final int FREE_CURSOR = 95;
	public static final int RECOLOR_CURSOR = 96;
	public static final int QUERY_EXTENSION = 98;

	public static final int GET_KEYBOARD_MAPPING = 101;
	
	public static final int SET_CLOSE_DOWN_MODE = 112;
	
	public static final int GET_MODIFIER_MAPPING = 119;
}
