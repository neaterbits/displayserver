package com.neaterbits.displayserver.windows;

import java.util.List;

public interface WindowsDisplayArea extends DisplayArea {

    Window getRootWindow();

    Window createWindow(Window parentWindow, WindowParameters parameters, WindowAttributes attributes);

    void disposeWindow(Window window);

    List<Window> getSubWindowsInOrder(Window window);

}
