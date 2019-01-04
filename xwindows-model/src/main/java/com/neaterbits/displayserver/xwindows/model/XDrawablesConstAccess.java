package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public interface XDrawablesConstAccess {

    XDrawable findDrawable(DRAWABLE drawable);
    
    XWindow findPixmapWindow(PIXMAP pixmap);
    
}
