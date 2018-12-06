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
    
    private final Map<XWindow, XWindowsConnectionState> connectionByWindow;


    XWindows() {
        
        this.rootWindows = new WindowMap();
        this.clientWindows = new WindowMap();
        
        this.connectionByWindow = new HashMap<>();
    }
    
    void addRootWindow(XWindow window) {
        
        Objects.requireNonNull(window);
        
        if (!window.isRootWindow()) {
            throw new IllegalArgumentException();
        }
        
        rootWindows.addToMaps(window);
    }

    
    void addWindow(XWindow window, XWindowsConnectionState creatingConnection) {

        Objects.requireNonNull(window);
        Objects.requireNonNull(creatingConnection);
        
        if (window.isRootWindow()) {
            throw new IllegalArgumentException();
        }
        
        connectionByWindow.put(window, creatingConnection);

        clientWindows.addToMaps(window);
    }

    void removeClientWindow(XWindow window) {
        
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
    public XWindow getClientWindow(DRAWABLE windowResource) {
        return clientWindows.getWindow(windowResource);
    }
    @Override
    public XWindow getClientWindow(WINDOW windowResource) {
        return clientWindows.getWindow(windowResource.toDrawable());
    }

    @Override
    public XWindow getClientWindow(Window window) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public XWindow findRootWindowOf(WINDOW window) {
        
        Objects.requireNonNull(window);
        
        final XWindow result;
        
        final XWindow rootWindow = rootWindows.getWindow(window);
        
        if (rootWindow != null) {
            result = rootWindow;
        }
        else {
            
            final XWindow xWindow = clientWindows.getWindow(window);
            
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
        private final Map<DRAWABLE, XWindow> drawableToWindow;
        private final Map<XWindow, DRAWABLE> windowToDrawable;

        public WindowMap() {
            this.drawableToWindow = new HashMap<>();
            this.windowToDrawable = new HashMap<>();
        }

        XWindow getWindow(WINDOW windowResource) {
            return getWindow(windowResource.toDrawable());
        }

        XWindow getWindow(DRAWABLE windowResource) {
            
            Objects.requireNonNull(windowResource);
            
            return drawableToWindow.get(windowResource);
        }
        
        void addToMaps(XWindow window) {
            final DRAWABLE drawable = window.getWINDOW().toDrawable();
            
            drawableToWindow.put(drawable, window);
            windowToDrawable.put(window, drawable);
        }
        
        boolean remove(XWindow window) {
            
            final DRAWABLE drawable = window.getWINDOW().toDrawable();
            final XWindow removedWindow = drawableToWindow.remove(drawable);

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
