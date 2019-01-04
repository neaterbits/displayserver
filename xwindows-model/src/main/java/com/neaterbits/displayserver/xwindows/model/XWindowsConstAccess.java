package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

public interface XWindowsConstAccess<T extends XWindow> {

    T getClientWindow(WINDOW windowResource);

    XWindow getClientOrRootWindow(WINDOW windowResource);

    XWindow findRootWindowOf(WINDOW windowResource);
    
    T getClientWindow(Window window);
}
