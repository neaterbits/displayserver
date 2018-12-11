package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.DisplayArea;

final class XScreen {

    private final DisplayArea displayArea;
    private final XWindow rootWindow;

    XScreen(DisplayArea displayArea, XWindow rootWindow) {

        Objects.requireNonNull(displayArea);
        Objects.requireNonNull(rootWindow);
        
        this.displayArea = displayArea;
        this.rootWindow = rootWindow;
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
}
