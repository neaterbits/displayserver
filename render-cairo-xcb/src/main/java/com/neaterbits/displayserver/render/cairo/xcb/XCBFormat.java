package com.neaterbits.displayserver.render.cairo.xcb;

public final class XCBFormat extends XCBReference {

    XCBFormat(long reference) {
        super(reference);
    }

    public int getDepth() {
        return XCBNative.format_depth(getXCBReference());
    }
    
    public int getBitsPerPixel() {
        return XCBNative.format_bits_per_pixel(getXCBReference());
    }

    public int getScanlinePad() {
        return XCBNative.format_scanline_pad(getXCBReference());
    }

    @Override
    public void dispose() {
        
    }
}
