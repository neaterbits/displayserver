package com.neaterbits.displayserver.render.cairo.xcb;

import com.neaterbits.displayserver.util.NativeReference;

public abstract class XCBReference extends NativeReference {

    public XCBReference(long reference) {
        super(reference);
    }

    final long getXCBReference() {
        return getReference();
    }
}
