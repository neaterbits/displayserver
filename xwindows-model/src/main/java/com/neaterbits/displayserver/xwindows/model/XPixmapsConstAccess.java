package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public interface XPixmapsConstAccess {
    
    boolean hasPixmap(PIXMAP pixmap);
    
    XPixmap getPixmap(PIXMAP pixmap);
    
    DRAWABLE getOwnerDrawable(PIXMAP pixmap);
}
