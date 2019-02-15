package com.neaterbits.displayserver.windows;

import java.util.List;

import com.neaterbits.displayserver.layers.LayerRegion;

public interface WindowManagement {
    
    Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes);

    LayerRegion showWindow(Window window);
    
    void disposeWindow(Window window);

    List<Window> getSubWindowsInOrder(Window window);

    TranslatedCoordinates translateCoordinates(Window window, int x, int y);
}

