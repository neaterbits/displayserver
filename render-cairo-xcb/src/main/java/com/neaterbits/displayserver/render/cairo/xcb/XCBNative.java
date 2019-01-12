package com.neaterbits.displayserver.render.cairo.xcb;

public class XCBNative {

    static native long xcb_connect(String display);

    static native long xcb_connect_display(String display, String authMethod, byte [] authData);
    
    static native void xcb_disconnect(long connection);

    static native int xcb_generate_id(long connection);
    
    static native void xcb_flush(long connection);
    
    static native long xcb_get_setup(long connection);
    
    static native int setup_status(long setup);
    static native int setup_protocol_major_version(long setup);
    static native int setup_protocol_minor_version(long setup);
    static native long setup_release_number(long setup);
    static native long setup_resource_id_base(long setup);
    static native long setup_resource_id_mask(long setup);
    static native long setup_motion_buffer_size(long setup);
    static native String setup_vendor(long setup);
    static native int setup_maximum_request_length(long setup);
    static native int setup_image_byte_order(long setup);
    static native int setup_bitmap_format_bit_order(long setup);
    static native int setup_bitmap_format_scanline_unit(long setup);
    static native int setup_bitmap_format_scanline_pad(long setup);
    static native int setup_min_keycode(long setup);
    static native int setup_max_keycode(long setup);

    static native long [] setup_get_formats(long setup);
    
    static native int format_depth(long format);
    static native int format_bits_per_pixel(long format);
    static native int format_scanline_pad(long format);
    
    static native long [] setup_get_screens(long setup);
    
    static native int screen_root(long screen);
    static native int screen_default_colormap(long screen);
    static native long screen_white_pixel(long screen);
    static native long screen_black_pixel(long screen);
    static native long screen_current_input_masks(long screen);
    static native int screen_width_in_pixels(long screen);
    static native int screen_height_in_pixels(long screen);
    static native int screen_width_in_millimeters(long screen);
    static native int screen_height_in_millimeters(long screen);
    static native int screen_min_installed_maps(long screen);
    static native int screen_max_installed_maps(long screen);
    static native int screen_root_visual(long screen);
    static native int screen_backing_stores(long screen);
    static native int screen_save_unders(long screen);
    static native int screen_root_depth(long screen);

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

    static native int xcb_send_request(long connection, byte [] vector, int opcode, boolean isvoid);

    static native byte [] xcb_wait_reply(long connection, int sequence_number);

    static native int xcb_wait_for_event(long connection);
    
    static native void test(long connect);
}
