package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

final class XWindowsWindow {

    private final Window window;
    
    private final WINDOW windowResource;
    private final WINDOW rootWindow;
    private final WINDOW parentWindow;
    
    private final CARD16 borderWidth;
    private final CARD16 windowClass;
    
    private WindowAttributes currentWindowAttributes;
    
    XWindowsWindow(
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(windowResource);
        Objects.requireNonNull(rootWindow);
        Objects.requireNonNull(parentWindow);
        Objects.requireNonNull(borderWidth);
        Objects.requireNonNull(windowClass);
        Objects.requireNonNull(currentWindowAttributes);
        
        this.window = window;
        this.windowResource = windowResource;
        this.rootWindow = rootWindow;
        this.parentWindow = parentWindow;
        this.borderWidth = borderWidth;
        this.windowClass = windowClass;
        this.currentWindowAttributes = currentWindowAttributes;
    }

    Window getWindow() {
        return window;
    }
    
    WINDOW getWINDOW() {
        return windowResource;
    }
    
    WINDOW getRootWINDOW() {
        return rootWindow;
    }
    
    WINDOW getParentWINDOW() {
        return parentWindow;
    }

    byte getDepth() {
        return (byte)window.getDepth();
    }

    short getX() {
        return (short)window.getPosition().getLeft();
    }
    
    short getY() {
        return (short)window.getPosition().getTop();
    }
    
    int getWidth() {
        return window.getSize().getWidth();
    }
    
    int getHeight() {
        return window.getSize().getHeight();
    }

    CARD16 getBorderWidth() {
        return borderWidth;
    }
    
    CARD16 getWindowClass() {
        return windowClass;
    }

    WindowAttributes getCurrentWindowAttributes() {
        return currentWindowAttributes;
    }
    
    void setCurrentWindowAttributes(WindowAttributes currentWindowAttributes) {
        this.currentWindowAttributes = currentWindowAttributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((window == null) ? 0 : window.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        XWindowsWindow other = (XWindowsWindow) obj;
        if (window == null) {
            if (other.window != null)
                return false;
        } else if (!window.equals(other.window))
            return false;
        return true;
    }
}
