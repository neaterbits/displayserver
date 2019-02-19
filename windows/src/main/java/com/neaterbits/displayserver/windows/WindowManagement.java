package com.neaterbits.displayserver.windows;

import java.util.List;

import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.layers.LayerRegions;

public interface WindowManagement {
    
    Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes);

    LayerRegion showWindow(Window window);

    LayerRegions hideWindow(Window window);

    LayerRegion getVisibleOrStoredRegion(Window window);
    
    void disposeWindow(Window window);

    List<Window> getSubWindowsInOrder(Window window);

    Window findWindowAt(WindowsDisplayArea displayArea, int x, int y);
    
    TranslatedCoordinates translateCoordinates(Window window, int x, int y);
}

