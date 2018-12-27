package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;

final class XVisuals implements XVisualsConstAccess {

    private final Map<VISUALID, XVisual> visualsById;
    
    XVisuals(Map<VISUALID, XVisual> map) {
        this.visualsById = new HashMap<>(map);
    }
    
    @Override
    public XVisual getVisual(VISUALID visual) {
        
        Objects.requireNonNull(visual);
        
        return visualsById.get(visual);
    }
}
