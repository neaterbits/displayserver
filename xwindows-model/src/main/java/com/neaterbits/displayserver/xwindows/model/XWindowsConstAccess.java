package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public interface XWindowsConstAccess<T extends XWindow> {

    T getClientWindow(WINDOW windowResource);

    T getClientWindow(DRAWABLE windowResource);

    XWindow getClientOrRootWindow(WINDOW windowResource);

    XWindow getClientOrRootWindow(DRAWABLE windowResource);
    
    XWindow findRootWindowOf(WINDOW windowResource);
    
    T getClientWindow(Window window);
}
