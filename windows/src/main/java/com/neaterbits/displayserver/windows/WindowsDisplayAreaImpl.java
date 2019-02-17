package com.neaterbits.displayserver.windows;

import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.layers.LayerRegion;
import com.neaterbits.displayserver.layers.LayerRegions;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;

public final class WindowsDisplayAreaImpl implements WindowsDisplayArea {

    private final DisplayArea displayArea;
    private final Windows windows;

    public WindowsDisplayAreaImpl(DisplayArea displayArea) {
    
        Objects.requireNonNull(displayArea);
        
        this.displayArea = displayArea;
        this.windows = new Windows(this);
    }

    @Override
    public Size getSize() {
        return displayArea.getSize();
    }

    @Override
    public Size getSizeInMillimeters() {
        return displayArea.getSizeInMillimeters();
    }

    @Override
    public int getDepth() {
        return displayArea.getDepth();
    }

    @Override
    public PixelFormat getPixelFormat() {
        return displayArea.getPixelFormat();
    }

    @Override
    public List<ViewPort> getViewPorts() {
        return displayArea.getViewPorts();
    }

    @Override
    public OffscreenSurface allocateOffscreenSurface(Size size, PixelFormat pixelFormat) {
        return displayArea.allocateOffscreenSurface(size, pixelFormat);
    }

    @Override
    public void freeOffscreenSurface(OffscreenSurface surface) {
        displayArea.freeOffscreenSurface(surface);
    }

    @Override
    public boolean sameAs(DisplayArea other) {
        return displayArea.sameAs(other);
    }

    @Override
    public Window getRootWindow() {
        return windows.getRootWindow();
    }
    
    @Override
    public Window createWindow(Window parentWindow, WindowParameters parameters, WindowAttributes attributes) {
        
        return windows.createWindow(parentWindow, parameters, attributes);
    }
    
    @Override
    public LayerRegion showWindow(Window window) {

        return windows.showWindow(window);
    }
    
    @Override
    public LayerRegions hideWindow(Window window) {
        return windows.hideWindow(window);
    }

    @Override
    public LayerRegion getVisibleOrStoredRegion(Window window) {
        return windows.getVisibleOrStoredRegion(window);
    }

    @Override
    public void disposeWindow(Window window) {
        windows.disposeWindow(window);
    }
    
    @Override
    public List<Window> getSubWindowsInOrder(Window window) {
        return windows.getSubWindowsInOrder(window);
    }

    @Override
    public TranslatedCoordinates translateCoordinates(Window window, int x, int y) {
        return windows.translateCoordinates(window, x, y);
    }
}
