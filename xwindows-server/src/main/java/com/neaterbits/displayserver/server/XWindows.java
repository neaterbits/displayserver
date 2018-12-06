package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

final class XWindows implements XWindowsConstAccess {

    private final WindowMap rootWindows;
    private final WindowMap clientWindows;
    
    private final Map<XWindowsWindow, XWindowsConnectionState> connectionByWindow;


    XWindows() {
        
        this.rootWindows = new WindowMap();
        this.clientWindows = new WindowMap();
        
        this.connectionByWindow = new HashMap<>();
    }
    
    void addRootWindow(XWindowsWindow window) {
        
        Objects.requireNonNull(window);
        
        if (!window.isRootWindow()) {
            throw new IllegalArgumentException();
        }
        
        rootWindows.addToMaps(window);
    }

    
    void addWindow(XWindowsWindow window, XWindowsConnectionState creatingConnection) {

        Objects.requireNonNull(window);
        Objects.requireNonNull(creatingConnection);
        
        if (window.isRootWindow()) {
            throw new IllegalArgumentException();
        }
        
        connectionByWindow.put(window, creatingConnection);

        clientWindows.addToMaps(window);
    }

    void removeClientWindow(XWindowsWindow window) {
        
        Objects.requireNonNull(window);
        
        if (window.isRootWindow()) {
            throw new IllegalArgumentException();
        }

        if (clientWindows.remove(window)) {
            if (connectionByWindow.remove(window) == null) {
                throw new IllegalStateException();
            }
        }
        else {
            if (connectionByWindow.remove(window) != null) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public XWindowsWindow getClientWindow(DRAWABLE windowResource) {
        return clientWindows.getWindow(windowResource);
    }
    @Override
    public XWindowsWindow getClientWindow(WINDOW windowResource) {
        return clientWindows.getWindow(windowResource.toDrawable());
    }

    @Override
    public XWindowsWindow getClientWindow(Window window) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public XWindowsWindow findRootWindowOf(WINDOW window) {
        
        Objects.requireNonNull(window);
        
        final XWindowsWindow result;
        
        final XWindowsWindow rootWindow = rootWindows.getWindow(window);
        
        if (rootWindow != null) {
            result = rootWindow;
        }
        else {
            
            final XWindowsWindow xWindow = clientWindows.getWindow(window);
            
            if (xWindow == null) {
                result = null;
            }
            else {
                result = rootWindows.getWindow(xWindow.getRootWINDOW());
            }
        }
        
        return result;
    }
    
    private static class WindowMap {
        private final Map<DRAWABLE, XWindowsWindow> drawableToWindow;
        private final Map<XWindowsWindow, DRAWABLE> windowToDrawable;

        public WindowMap() {
            this.drawableToWindow = new HashMap<>();
            this.windowToDrawable = new HashMap<>();
        }

        XWindowsWindow getWindow(WINDOW windowResource) {
            return getWindow(windowResource.toDrawable());
        }

        XWindowsWindow getWindow(DRAWABLE windowResource) {
            
            Objects.requireNonNull(windowResource);
            
            return drawableToWindow.get(windowResource);
        }
        
        void addToMaps(XWindowsWindow window) {
            final DRAWABLE drawable = window.getWINDOW().toDrawable();
            
            drawableToWindow.put(drawable, window);
            windowToDrawable.put(window, drawable);
        }
        
        boolean remove(XWindowsWindow window) {
            
            final DRAWABLE drawable = window.getWINDOW().toDrawable();
            final XWindowsWindow removedWindow = drawableToWindow.remove(drawable);

            final boolean removed;
            
            if (removedWindow != null) {
                
                if (window != removedWindow) {
                    throw new IllegalStateException();
                }
                
                windowToDrawable.remove(removedWindow);

                removed = true;
            }
            else {
                removed = false;
            }
            
            return removed;
        }
    }
}
