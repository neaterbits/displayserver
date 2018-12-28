package com.neaterbits.displayserver.render.cairo;

import com.neaterbits.displayserver.util.NativeReference;

public abstract class CairoReference extends NativeReference {

    CairoReference(long reference) {
        super(reference);
    }
    
    final long getCairoReference() {
        return getReference();
    }
}
