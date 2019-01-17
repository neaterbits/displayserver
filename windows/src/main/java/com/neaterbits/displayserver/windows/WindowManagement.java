package com.neaterbits.displayserver.windows;

import java.util.List;

public interface WindowManagement {
    
    Window createWindow(Window parentWindow, WindowParameters windowParameters, WindowAttributes windowAttributes);

    void disposeWindow(Window window);

    List<Window> getSubWindowsInOrder(Window window);
}
