package com.neaterbits.displayserver.xwindows.model;

import java.util.Set;

import com.neaterbits.displayserver.buffers.PixelFormat;

public interface XScreensConstAccess {

    int getNumberOfScreens();
    
    XScreen getScreen(int screenNo);
    
    Set<PixelFormat> getDistinctPixelFormats();
    
}
