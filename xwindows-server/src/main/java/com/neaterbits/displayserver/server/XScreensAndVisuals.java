package com.neaterbits.displayserver.server;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;

final class XScreensAndVisuals {

    private final List<XScreen> screens;
    private final Map<VISUALID, XVisual> visuals;
    
    XScreensAndVisuals(List<XScreen> screens, Map<VISUALID, XVisual> visuals) {
        
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
