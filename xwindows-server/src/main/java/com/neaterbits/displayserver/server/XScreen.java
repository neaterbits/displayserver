package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.DisplayArea;

final class XScreen {

    private final DisplayArea displayArea;
    private final XWindow rootWindow;
    private final List<XVisual> visuals;

    XScreen(DisplayArea displayArea, XWindow rootWindow, Collection<XVisual> visuals) {

        Objects.requireNonNull(displayArea);
        Objects.requireNonNull(rootWindow);
        
        this.displayArea = displayArea;
        this.rootWindow = rootWindow;
        this.visuals = new ArrayList<>(visuals);
    }

    DisplayArea getDisplayArea() {
        return displayArea;
    }

    WINDOW getRootWINDOW() {
        return rootWindow.getWINDOW();
    }

    XWindow getRootWindow() {
        return rootWindow;
    }

    List<XVisual> getVisuals() {
        return visuals;
    }
}
