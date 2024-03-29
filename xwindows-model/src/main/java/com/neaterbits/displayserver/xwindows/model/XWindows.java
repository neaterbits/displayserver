package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.protocol.exception.WindowException;
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
    
    @Override
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

    public boolean removeClientWindow(XWindow xWindow) {
        
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

        XWindow result = clientWindows.getWindow(windowResource);
        
        if (result == null) {
            result = rootWindows.getWindow(windowResource);
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T getClientWindow(WINDOW windowResource) {
        return (T)clientWindows.getWindow(windowResource);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getClientOrRootWindow(Window window) {

        Objects.requireNonNull(window);

        return (T)xWindowByWindow.get(window);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T getClientWindow(Window window) {
        
        Objects.requireNonNull(window);
        
        final T xWindow = (T)xWindowByWindow.get(window);
        
        return xWindow.isRootWindow() ? null : xWindow;
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
    
    @Override
    public final Collection<XWindow> getAllSubWindows(WINDOW windowResource) throws WindowException {

        Objects.requireNonNull(windowResource);
        
        final Collection<XWindow> windows;
        
        if (rootWindows.contains(windowResource)) {
            windows = rootWindows.getAllSubWindows(windowResource);
        }
        else {
            windows = clientWindows.getAllSubWindows(windowResource);
        }
        
        return windows;
    }

    private static class WindowMap {
        private final Map<WINDOW, XWindow> resourceToWindow;
        private final Map<XWindow, WINDOW> windowToResource;

        public WindowMap() {
            this.resourceToWindow = new HashMap<>();
            this.windowToResource = new HashMap<>();
        }

        boolean contains(WINDOW windowResource) {
            
            Objects.requireNonNull(windowResource);
            
            return resourceToWindow.containsKey(windowResource);
        }

        XWindow getWindow(WINDOW windowResource) {
            
            Objects.requireNonNull(windowResource);
            
            return resourceToWindow.get(windowResource);
        }
        
        void addToMaps(XWindow window) {
            final WINDOW resource = window.getWINDOW();
            
            resourceToWindow.put(resource, window);
            windowToResource.put(window, resource);
        }
        
        boolean remove(XWindow window) {
            
            final WINDOW resource = window.getWINDOW();
            final XWindow removedWindow = resourceToWindow.remove(resource);

            final boolean removed;
            
            if (removedWindow != null) {
                
                if (window != removedWindow) {
                    throw new IllegalStateException();
                }
                
                windowToResource.remove(removedWindow);

                removed = true;
            }
            else {
                removed = false;
            }
            
            return removed;
        }
        
        Collection<XWindow> getAllSubWindows(WINDOW windowResource) throws WindowException {
            
            Objects.requireNonNull(windowResource);
            
            final Set<XWindow> windows = new HashSet<>();
            
            final XWindow xWindow = resourceToWindow.get(windowResource);
            
            if (xWindow == null) {
                throw new WindowException("No such window", windowResource);
            }
            
            getAllSubWindows(xWindow, windows);
            
            return windows;
        }
    
        private void getAllSubWindows(XWindow xWindow, Set<XWindow> windows) {

            Objects.requireNonNull(xWindow);
            
            for (XWindow xw : windowToResource.keySet()) {
                if (xw.getParentWINDOW().equals(xWindow.getWINDOW())) {
                    
                    if (windows.contains(xw)) {
                        throw new IllegalStateException();
                    }
                    
                    windows.add(xw);
                    
                    getAllSubWindows(xw, windows);
                }
            }
        }
    }
}
