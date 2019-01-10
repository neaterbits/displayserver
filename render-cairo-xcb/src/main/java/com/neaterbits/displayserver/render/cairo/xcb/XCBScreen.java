package com.neaterbits.displayserver.render.cairo.xcb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class XCBScreen extends XCBReference {

    XCBScreen(long reference) {
        super(reference);
    }
    
    public List<XCBDepth> getDepths() {
        
        final long [] depthReferences = XCBNative.screen_get_depths(getXCBReference());
        
        return Arrays.stream(depthReferences)
                .mapToObj(XCBDepth::new)
                .collect(Collectors.toList());
    }

    public int getRoot() {
        return XCBNative.screen_root(getXCBReference());
    }
    
    public int getDefaultColorMap() {
        return XCBNative.screen_default_colormap(getXCBReference());
    }
    
    public long getWhitePixel() {
        return XCBNative.screen_white_pixel(getXCBReference());
    }
    
    public long getBlackPixel() {
        return XCBNative.screen_black_pixel(getXCBReference());
    }
    
    public long getCurrentInputMasks() {
        return XCBNative.screen_current_input_masks(getXCBReference());
    }
    
    public int getWidthInPixels() {
        return XCBNative.screen_width_in_pixels(getXCBReference());
    }
    
    public int getHeightInPixels() {
        return XCBNative.screen_height_in_pixels(getXCBReference());
    }
    
    public int getWidthInMillimiters() {
        return XCBNative.screen_width_in_millimeters(getXCBReference());
    }
    
    public int getHeightInMillimiters() {
        return XCBNative.screen_height_in_millimeters(getXCBReference());
    }
    
    public int getMinInstalledMaps() {
        return XCBNative.screen_min_installed_maps(getXCBReference());
    }
    
    public int getMaxInstalledMaps() {
        return XCBNative.screen_max_installed_maps(getXCBReference());
    }
    
    public int getRootVisual() {
        return XCBNative.screen_root_visual(getXCBReference());
    }
    
    public int getBackingStores() {
        return XCBNative.screen_backing_stores(getXCBReference());
    }
    
    public int getSaveUnders() {
        return XCBNative.screen_save_unders(getXCBReference());
    }
    
    public int getRootDepth() {
        return XCBNative.screen_root_depth(getXCBReference());
    }

    @Override
    public void dispose() {
        
    }
}
