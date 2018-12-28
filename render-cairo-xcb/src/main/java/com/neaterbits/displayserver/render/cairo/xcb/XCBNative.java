package com.neaterbits.displayserver.render.cairo.xcb;

public class XCBNative {

    static native long xcb_connect(String display);

    static native long xcb_connect_display(String display, String authMethod, byte [] authData);
    
    static native void xcb_disconnect(long connection);

    static native void xcb_flush(long connection);
    
    static native long xcb_get_setup(long connection);
    
    static native long [] setup_get_screens(long setup);
    
    static native long [] screen_get_depths(long screen);

    static native byte depth_get_depth(long depth);

    static native long [] depth_get_visuals(long depth);

    
    static native int  visual_get_visual_id(long visual);
    static native byte visual_get_class(long visual);
    static native byte visual_get_bits_per_rgb_value(long visual);
    static native int  visual_get_colormap_entries(long visual);
    static native int  visual_get_red_mask(long visual);
    static native int  visual_get_green_mask(long visual);
    static native int  visual_get_blue_mask(long visual);
    
    static native long cairo_create_xcb_surface(long connection, int drawable, long visual, int width, int height);
}
