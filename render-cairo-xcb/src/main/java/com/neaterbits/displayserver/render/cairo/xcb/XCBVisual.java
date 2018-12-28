package com.neaterbits.displayserver.render.cairo.xcb;

public final class XCBVisual extends XCBReference {

    XCBVisual(long reference) {
        super(reference);
    }

    public int getVisualId() {
        return XCBNative.visual_get_visual_id(getXCBReference());
    }
    
    public byte getVisualClass() {
        return XCBNative.visual_get_class(getXCBReference());
    }
    
    public byte getBitsPerRGBValue() {
        return XCBNative.visual_get_bits_per_rgb_value(getXCBReference());
    }
    
    public int getColormapEntries() {
        return XCBNative.visual_get_colormap_entries(getXCBReference());
    }
    
    public int getRedMask() {
        return XCBNative.visual_get_red_mask(getXCBReference());
    }
    
    public int getFreenMask() {
        return XCBNative.visual_get_green_mask(getXCBReference());
    }
    
    public int getBlueMask() {
        return XCBNative.visual_get_blue_mask(getXCBReference());
    }
    
    @Override
    public void dispose() {
        
    }

    @Override
    public String toString() {
        return "XCBVisual [getVisualId()=" + getVisualId() + ", getVisualClass()=" + getVisualClass()
                + ", getBitsPerRGBValue()=" + getBitsPerRGBValue() + ", getColormapEntries()=" + getColormapEntries()
                + ", getRedMask()=" + getRedMask() + ", getFreenMask()=" + getFreenMask() + ", getBlueMask()="
                + getBlueMask() + "]";
    }
}
