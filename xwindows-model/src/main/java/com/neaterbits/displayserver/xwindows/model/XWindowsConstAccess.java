package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;

import com.neaterbits.displayserver.protocol.exception.WindowException;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public interface XWindowsConstAccess<T extends XWindow> {

    T getClientWindow(WINDOW windowResource);

    XWindow getClientOrRootWindow(WINDOW windowResource);

    XWindow findRootWindowOf(WINDOW windowResource);

    T getClientOrRootWindow(Window window);

    T getClientWindow(Window window);

    Collection<XWindow> getAllSubWindows(WINDOW windowResource) throws WindowException;
    
    Integer getScreenForWindow(WINDOW window);
}
