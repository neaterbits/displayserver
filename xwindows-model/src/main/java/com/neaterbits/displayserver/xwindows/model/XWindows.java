package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public class XWindows<T extends XWindow> extends XResources<XWindow> implements XWindowsConstAccess<T> {

    private final Map<WINDOW, Integer> screenByRootWindow;
    
    private final WindowMap rootWindows;
    private final WindowMap clientWindows;
    
    private final Map<Window, XWindow> xWindowByWindow;

    protected XWindows() {
        
        this.screenByRootWindow = new HashMap<>();
        
        this.rootWindows = new WindowMap();
        this.clientWindows = new WindowMap();
        
        this.xWindowByWindow = new HashMap<>();
    }
    
    @Override
    public final Collection<XWindow> getResources() {
        return Collections.unmodifiableCollection(xWindowByWindow.values());
    }

    public final void addRootWindow(int screen, XWindow xWindow) {
        
        Objects.requireNonNull(xWindow);
        
        if (!xWindow.isRootWindow()) {
            throw new IllegalArgumentException();
        }
        
        screenByRootWindow.put(xWindow.getWINDOW(), screen);
        
        rootWindows.addToMaps(xWindow);

        addWindowToXWindowMapping(xWindow);
    }
    
    public final Integer getScreenForWindow(WINDOW window) {
     
        Objects.requireNonNull(window);
        
        final XWindow rootWindow = findRootWindowOf(window);
        
        return rootWindow != null ? screenByRootWindow.get(rootWindow.getWINDOW()) : null;
    }
    
    protected final void addClientWindow(XWindow xWindow) {

        Objects.requireNonNull(xWindow);
        
        if (xWindow.isRootWindow()) {
            throw new IllegalArgumentException();
        }

        clientWindows.addToMaps(xWindow);
        
        addWindowToXWindowMapping(xWindow);
    }
    
    private void addWindowToXWindowMapping(XWindow xWindow) {
        xWindowByWindow.put(xWindow.getWindow(), xWindow);
    }

    protected boolean removeClientWindow(XWindow xWindow) {
        
        Objects.requireNonNull(xWindow);
        
        if (xWindow.isRootWindow()) {
            throw new IllegalArgumentException();
        }

        final boolean removed = clientWindows.remove(xWindow);
        
        xWindowByWindow.remove(xWindow.getWindow());
        
        return removed;
    }

    @Override
    public final XWindow getClientOrRootWindow(WINDOW windowResource) {

        return getClientOrRootWindow(windowResource.toDrawable());
    }
    
    @Override
    public final XWindow getClientOrRootWindow(DRAWABLE windowResource) {

        XWindow result = clientWindows.getWindow(windowResource);
        
        if (result == null) {
            result = rootWindows.getWindow(windowResource);
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T getClientWindow(WINDOW windowResource) {
        return (T)clientWindows.getWindow(windowResource.toDrawable());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T getClientWindow(DRAWABLE windowResource) {
        return (T)clientWindows.getWindow(windowResource);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T getClientWindow(Window window) {
        
        Objects.requireNonNull(window);
        
        return (T)xWindowByWindow.get(window);
    }

    @Override
    public final XWindow findRootWindowOf(WINDOW window) {
        
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
