package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.COLORMAP;

public interface XColormapsConstAccess {
    
    boolean hasColormap(COLORMAP resource);

    XColormap getColormap(COLORMAP resource);
}
