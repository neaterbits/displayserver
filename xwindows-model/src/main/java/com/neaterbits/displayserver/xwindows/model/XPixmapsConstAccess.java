package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public interface XPixmapsConstAccess {
    
    XPixmap getPixmap(PIXMAP drawable);
    
    DRAWABLE getOwnerDrawable(PIXMAP pixmapDrawable);
}
