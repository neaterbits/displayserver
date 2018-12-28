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

    @Override
    public void dispose() {
        
    }
}
