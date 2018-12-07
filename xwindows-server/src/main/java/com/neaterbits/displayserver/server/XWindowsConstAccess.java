package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

interface XWindowsConstAccess {

    XWindow getClientWindow(WINDOW windowResource);

    XWindow getClientWindow(DRAWABLE windowResource);

    XWindow getClientOrRootWindow(WINDOW windowResource);

    XWindow getClientOrRootWindow(DRAWABLE windowResource);
    
    XWindow findRootWindowOf(WINDOW windowResource);
    
    XWindow getClientWindow(Window window);
}
