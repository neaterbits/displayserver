package com.neaterbits.displayserver.windows;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.OffscreenBufferProvider;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;

public final class DisplayAreaImpl implements DisplayAreaWindows {

    private final DisplayAreaConfig config;
    private final Size size;
    private final Size sizeInMillimeters;
    private final int depth;
    private final PixelFormat pixelFormat;
    private final OffscreenBufferProvider offscreenBufferProvider;
    private final List<ViewPort> viewPorts;
    
    private final Windows windows;
    
    public DisplayAreaConfig getConfig() {
        return config;
    }

    DisplayAreaImpl(
            DisplayAreaConfig config,
            Size size,
            Size sizeInMillimeters,
            int depth,
            PixelFormat pixelFormat,
            List<ViewPort> viewPorts,
            OffscreenBufferProvider offscreenBufferProvider,
            WindowEventListener windowEventListener) {
        
        Objects.requireNonNull(config);
        Objects.requireNonNull(size);
        Objects.requireNonNull(sizeInMillimeters);
        Objects.requireNonNull(pixelFormat);
        Objects.requireNonNull(viewPorts);
        Objects.requireNonNull(offscreenBufferProvider);
        
        if (viewPorts.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        this.config = config;
        this.size = size;
        this.sizeInMillimeters = sizeInMillimeters;
        this.depth = depth;
        this.pixelFormat = pixelFormat;
        this.viewPorts = viewPorts;
        this.offscreenBufferProvider = offscreenBufferProvider;
        
        this.windows = new Windows(this, windowEventListener);
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public Size getSizeInMillimeters() {
        return sizeInMillimeters;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    @Override
    public List<ViewPort> getViewPorts() {
        return Collections.unmodifiableList(viewPorts);
    }
    
    @Override
    public OffscreenBufferProvider getOffscreenBufferProvider() {
        return offscreenBufferProvider;
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
    public void disposeWindow(Window window) {
        windows.disposeWindow(window);
    }
    
    @Override
    public List<Window> getSubWindowsInOrder(Window window) {
        return windows.getSubWindowsInOrder(window);
    }

}
