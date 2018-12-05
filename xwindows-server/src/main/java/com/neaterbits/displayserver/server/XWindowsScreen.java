package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Screen;

final class XWindowsScreen {

    private final Screen screen;
    private final WINDOW rootWindow;

    XWindowsScreen(Screen screen, WINDOW rootWindow) {

        Objects.requireNonNull(screen);
        Objects.requireNonNull(rootWindow);
        
        this.screen = screen;
        this.rootWindow = rootWindow;
    }

    Screen getScreen() {
        return screen;
    }

    WINDOW getRootWindow() {
        return rootWindow;
    }
}
