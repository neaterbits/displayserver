package com.neaterbits.displayserver.render.cairo;

public class CairoNative {

    // cairo_t
    static native long cairo_create(long surface);
    
    static native void cairo_destroy(long cr);
    
    static native void cairo_set_source_rgb(long cr, double red, double green, double blue);
    
    static native void cairo_set_fill_rule(long cr, int fillRule);
    
    static native void cairo_fill(long cr);

    static native void cairo_paint(long cr);
    
    static native void cairo_stroke(long cr);
    static native void cairo_stroke_preserve(long cr);
    
    // paths
    static native void cairo_new_path(long cr);

    static native void cairo_move_to(long cr, double x, double y);

    static native void cairo_rel_move_to(long cr, double dx, double dy);

    static native void cairo_line_to(long cr, double x, double y);

    static native void cairo_rel_line_to(long cr, double dx, double dy);

    static native void cairo_rectangle(long cr, double x, double y, double width, double height);
    
    // cairo_surface_t
    
    static native void cairo_surface_destroy(long surface);
    static native void cairo_surface_flush(long surface);
    
}
