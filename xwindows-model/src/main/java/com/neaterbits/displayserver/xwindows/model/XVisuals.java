package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class XVisuals implements XVisualsConstAccess {

    private final Map<VISUALID, XVisual> visualsById;
    
    public XVisuals(Map<VISUALID, XVisual> map) {
        this.visualsById = new HashMap<>(map);
    }
    
    @Override
    public XVisual getVisual(VISUALID visual) {
        
        Objects.requireNonNull(visual);
        
        System.out.println("## get visual " + visual + " from " + visualsById);
        
        return visualsById.get(visual);
    }
}
