package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class XColorMaps {

    private final Map<COLORMAP, XColorMap> colormaps;

    public XColorMaps() {
        this.colormaps = new HashMap<>();
    }
    
    public void add(COLORMAP resource, XColorMap colormap) {

        Objects.requireNonNull(resource);
        Objects.requireNonNull(colormap);
    
        colormaps.put(resource, colormap);
    }
    
    public boolean contains(COLORMAP resource) {
        
        Objects.requireNonNull(resource);

        return colormaps.containsKey(resource);
    }

    public XColorMap get(COLORMAP resource) {

        Objects.requireNonNull(resource);

        return colormaps.get(resource);
    }
    
    public void remove(COLORMAP resource) {
        
        Objects.requireNonNull(resource);
        
        colormaps.remove(resource);
    }
}
