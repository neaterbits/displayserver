package com.neaterbits.displayserver.xwindows.model;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public interface XPixmapsConstAccess {
    
    default XPixmap getPixmap(PIXMAP pixmap) {
        return getPixmap(pixmap.toDrawable());
    }
    
    XPixmap getPixmap(DRAWABLE drawable);
    
}
