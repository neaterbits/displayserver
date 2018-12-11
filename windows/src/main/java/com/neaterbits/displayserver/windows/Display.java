package com.neaterbits.displayserver.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Display {

    private final List<DisplayAreaWindows> displayAreas;
    
    public Display(Collection<DisplayAreaWindows> displayAreas) {
        
        Objects.requireNonNull(displayAreas);
        
        this.displayAreas = new ArrayList<>(displayAreas);
    }
    
    public Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes) {

        final DisplayAreaWindows displayArea = parentWindow.getDisplayArea();
        
        if (!displayAreas.contains(displayArea)) {
            throw new IllegalArgumentException();
        }
        
        return displayArea.createWindow(parentWindow, windowParameters, windowAttributes);
    }
    
    public void disposeWindow(Window window) {
        Objects.requireNonNull(window);
        
        window.getDisplayArea().disposeWindow(window);
    }
    
    public List<Window> getSubWindowsInOrder(Window window) {
        Objects.requireNonNull(window);
        
        return window.getDisplayArea().getSubWindowsInOrder(window);
    }
}
