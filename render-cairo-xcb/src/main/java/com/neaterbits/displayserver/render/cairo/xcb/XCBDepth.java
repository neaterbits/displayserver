package com.neaterbits.displayserver.render.cairo.xcb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XCBDepth extends XCBReference {

    XCBDepth(long reference) {
        super(reference);
    }
    
    public byte getDepth() {
        return XCBNative.depth_get_depth(getXCBReference());
    }

    public List<XCBVisual> getVisuals() {
        
        final long [] visualReferences = XCBNative.depth_get_visuals(getXCBReference());
        
        return Arrays.stream(visualReferences)
                .mapToObj(XCBVisual::new)
                .collect(Collectors.toList());
    }

    @Override
    public void dispose() {

    }
}
