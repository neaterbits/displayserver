package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Screen;

final class XScreen {

    private final Screen screen;
    private final XWindow rootWindow;

    XScreen(Screen screen, XWindow rootWindow) {

        Objects.requireNonNull(screen);
        Objects.requireNonNull(rootWindow);
        
        this.screen = screen;
        this.rootWindow = rootWindow;
    }

    Screen getScreen() {
        return screen;
    }

    WINDOW getRootWINDOW() {
        return rootWindow.getWINDOW();
    }

    XWindow getRootWindow() {
        return rootWindow;
    }
}
