package com.neaterbits.displayserver.render.cairo.xcb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class XCBSetup extends XCBReference {

    XCBSetup(long reference) {
        super(reference);
    }

    public List<XCBScreen> getScreens() {
        
        final long [] screenReferences = XCBNative.setup_get_screens(getXCBReference());
        
        return Arrays.stream(screenReferences)
                .mapToObj(XCBScreen::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public void dispose() {
        
    }
}
