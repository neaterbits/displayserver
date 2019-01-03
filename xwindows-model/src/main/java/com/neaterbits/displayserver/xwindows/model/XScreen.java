package com.neaterbits.displayserver.xwindows.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.DisplayArea;

public final class XScreen {

    private final int screenNo;
    private final DisplayArea displayArea;
    private final XWindow rootWindow;
    private final VISUALID rootVisual;
    private final List<XScreenDepth> depths;

    public XScreen(int screenNo, DisplayArea displayArea, XWindow rootWindow, VISUALID rootVisual, List<XScreenDepth> depths) {

        Objects.requireNonNull(displayArea);
        Objects.requireNonNull(rootWindow);
        Objects.requireNonNull(rootVisual);
        Objects.requireNonNull(depths);
        
        this.screenNo = screenNo;
        this.displayArea = displayArea;
        this.rootWindow = rootWindow;
        this.rootVisual = rootVisual;
        this.depths = Collections.unmodifiableList(new ArrayList<>(depths));
    }

    public int getScreenNo() {
        return screenNo;
    }

    public DisplayArea getDisplayArea() {
        return displayArea;
    }

    public WINDOW getRootWINDOW() {
        return rootWindow.getWINDOW();
    }

    XWindow getRootWindow() {
        return rootWindow;
    }

    public VISUALID getRootVisual() {
        return rootVisual;
    }

    public List<XScreenDepth> getDepths() {
        return depths;
    }
    
    public boolean supportsVisual(XVisual visual, XVisualsConstAccess visualsConstAccess) {
        
        Objects.requireNonNull(visual);
        Objects.requireNonNull(visualsConstAccess);
        
        return depths.stream()
                .flatMap(depth -> depth.getVisuals().stream())
                .map(visualId -> visualsConstAccess.getVisual(visualId))
                .anyMatch(v -> visual.equals(v));
    }
}
