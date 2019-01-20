package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class XColormaps implements XColormapsConstAccess {

    private final Map<COLORMAP, XColormap> colormaps;

    public XColormaps() {
        this.colormaps = new HashMap<>();
    }
    
    public void add(COLORMAP resource, XColormap colormap) {

        Objects.requireNonNull(resource);
        Objects.requireNonNull(colormap);
    
        colormaps.put(resource, colormap);
    }
    
    @Override
    public boolean hasColormap(COLORMAP resource) {
        
        Objects.requireNonNull(resource);

        return colormaps.containsKey(resource);
    }

    @Override
    public XColormap getColormap(COLORMAP resource) {

        Objects.requireNonNull(resource);

        return colormaps.get(resource);
    }
    
    public void remove(COLORMAP resource) {
        
        Objects.requireNonNull(resource);
        
        colormaps.remove(resource);
    }
}
