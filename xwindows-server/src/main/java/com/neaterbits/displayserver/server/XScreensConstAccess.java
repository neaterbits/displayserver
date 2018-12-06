package com.neaterbits.displayserver.server;

import java.util.Set;

import com.neaterbits.displayserver.buffers.PixelFormat;

interface XScreensConstAccess {

    int getNumberOfScreens();
    
    XScreen getScreen(int screenNo);
    
    Set<PixelFormat> getDistinctPixelFormats();
    
}
