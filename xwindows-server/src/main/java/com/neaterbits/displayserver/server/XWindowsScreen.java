package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Screen;

final class XWindowsScreen {

    private final Screen screen;
    private final WINDOW rootWINDOW;
    private final XWindow rootWindow;

    XWindowsScreen(Screen screen, WINDOW rootWINDOW, XWindow rootWindow) {

        Objects.requireNonNull(screen);
        Objects.requireNonNull(rootWINDOW);
        Objects.requireNonNull(rootWindow);
        
        this.screen = screen;
        this.rootWINDOW = rootWINDOW;
        this.rootWindow = rootWindow;
    }

    Screen getScreen() {
        return screen;
    }

    WINDOW getRootWINDOW() {
        return rootWINDOW;
    }

    XWindow getRootWindow() {
        return rootWindow;
    }
}
