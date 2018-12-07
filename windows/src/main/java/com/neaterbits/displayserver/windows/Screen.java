package com.neaterbits.displayserver.windows;

import java.util.List;

import com.neaterbits.displayserver.framebuffer.common.GraphicsScreen;

public final class Screen {

    private final Windows windows;
    
    public Screen(GraphicsScreen driverScreen, WindowEventListener windowEventListener) {
        this.windows = new Windows(this, driverScreen, windowEventListener);
    }

    public Window getRootWindow() {
        return windows.getRootWindow();
    }
    
    public GraphicsScreen getDriverScreen() {
        return windows.getDriverScreen();
    }
    
    public Window createWindow(Window parentWindow, WindowParameters parameters, WindowAttributes attributes) {
        return windows.createWindow(parentWindow, parameters, attributes);
    }
    
    public void disposeWindow(Window window) {
        windows.disposeWindow(window);
    }
    
    public List<Window> getSubWindowsInOrder(Window window) {
        return windows.getSubWindowsInOrder(window);
    }
}
