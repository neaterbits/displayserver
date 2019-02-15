package com.neaterbits.displayserver.windows;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.layers.LayerRegions;

public class Display implements WindowManagement {

    private final WindowsDisplayAreas displayAreas;
    
    public Display(WindowsDisplayAreas displayAreas) {
        
        Objects.requireNonNull(displayAreas);
        
        this.displayAreas = displayAreas;
    }
    
    @Override
    public Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes) {

        final WindowsDisplayArea displayArea = parentWindow.getDisplayArea();
        
        if (!displayAreas.contains(displayArea)) {
            throw new IllegalArgumentException();
        }
        
        return displayArea.createWindow(parentWindow, windowParameters, windowAttributes);
    }
    
    
    @Override
    public LayerRegion showWindow(Window window) {

        return window.getDisplayArea().showWindow(window);
    }

    @Override
    public LayerRegions hideWindow(Window window) {
        return window.getDisplayArea().hideWindow(window);
    }

    @Override
    public void disposeWindow(Window window) {
        Objects.requireNonNull(window);
        
        window.getDisplayArea().disposeWindow(window);
    }
    
    @Override
    public List<Window> getSubWindowsInOrder(Window window) {
        Objects.requireNonNull(window);
        
        return window.getDisplayArea().getSubWindowsInOrder(window);
    }

    @Override
    public TranslatedCoordinates translateCoordinates(Window window, int x, int y) {
        return window.getDisplayArea().translateCoordinates(window, x, y);
    }
}
