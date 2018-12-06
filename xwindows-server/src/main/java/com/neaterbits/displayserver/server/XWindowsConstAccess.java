package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

interface XWindowsConstAccess {

    XWindowsWindow getClientWindow(WINDOW windowResource);

    XWindowsWindow getClientWindow(DRAWABLE windowResource);

    XWindowsWindow findRootWindowOf(WINDOW windowResource);
    
    XWindowsWindow getClientWindow(Window window);
}
