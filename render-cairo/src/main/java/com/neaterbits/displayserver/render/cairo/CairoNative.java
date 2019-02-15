package com.neaterbits.displayserver.render.cairo;

public class CairoNative {

    // cairo_t
    static native long cairo_create(long surface);
    
    static native void cairo_destroy(long cr);

    static native int cairo_status(long cr);
    
    static native void cairo_set_operator(long cr, int operator);
    
    static native void cairo_set_source_rgb(long cr, double red, double green, double blue);

    static native void cairo_set_source_surface(long cr, long surface, double x, double y);
    
    static native void cairo_set_fill_rule(long cr, int fillRule);
    
    static native void cairo_clip(long cr);

    static native void cairo_reset_clip(long cr);

    static native void cairo_fill(long cr);

    static native void cairo_mask_surface(long cr, long surface, double surface_x, double surface_y);
    
    static native void cairo_paint(long cr);
    
    static native void cairo_stroke(long cr);
    static native void cairo_stroke_preserve(long cr);
    
    static native int get_cairo_operator_enum_value(String enumName);
    
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
    
    static native int cairo_surface_get_reference_count(long surface);
    
    // image surface
    
    static native long cairo_image_surface_create(int format, int width, int height);
    
    static native long [] cairo_image_surface_create_for_data(byte [] data, int format, int width, int height, int stride);
    
    static native void free_image_surface_data(long data);
    
    // png surface
    static native int cairo_surface_write_to_png(long surface, String filename);
    
    // cairo_format_t
    static native int get_cairo_format_enum_value(String enumName);
    
    static native int cairo_format_stride_for_width(int format, int width);

    // cairo_status_t
    static native int get_cairo_status_enum_value(String enumName);
}
