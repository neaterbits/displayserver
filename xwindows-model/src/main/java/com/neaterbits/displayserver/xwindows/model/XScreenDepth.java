package com.neaterbits.displayserver.xwindows.model;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class XScreenDepth {

    private final int depth;
    private final List<VISUALID> visuals;
    
    public XScreenDepth(int depth, List<VISUALID> visuals) {

        Objects.requireNonNull(visuals);
        
        if (visuals.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        this.depth = depth;
        this.visuals = visuals;
    }

    public int getDepth() {
        return depth;
    }
    
    public List<VISUALID> getVisuals() {
        return visuals;
    }
}
