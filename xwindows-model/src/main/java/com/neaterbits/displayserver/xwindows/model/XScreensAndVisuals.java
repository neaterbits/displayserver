package com.neaterbits.displayserver.xwindows.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class XScreensAndVisuals {

    private final List<XScreen> screens;
    private final Map<VISUALID, XVisual> visuals;
    
    public XScreensAndVisuals(List<XScreen> screens, Map<VISUALID, XVisual> visuals) {
        
        Objects.requireNonNull(screens);
        Objects.requireNonNull(visuals);
        
        this.screens = screens;
        this.visuals = visuals;
    }

    List<XScreen> getScreens() {
        return screens;
    }

    Map<VISUALID, XVisual> getVisuals() {
        return visuals;
    }
}
