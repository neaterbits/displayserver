package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Display {

    private final List<Screen> screens;
    
    public Display(Collection<Screen> screens) {
        
        Objects.requireNonNull(screens);
        
        this.screens = new ArrayList<>(screens);
    }
    
    public Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes) {

        final Screen screen = parentWindow.getScreen();
        
        if (!screens.contains(screen)) {
            throw new IllegalArgumentException();
        }
        
        return screen.createWindow(parentWindow, windowParameters, windowAttributes);
    }
    
    public void disposeWindow(Window window) {
        Objects.requireNonNull(window);
        
        window.getScreen().disposeWindow(window);
    }
}
