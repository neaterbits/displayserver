package com.neaterbits.displayserver.xwindows.model.render;

import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.util.Disposable;
import com.neaterbits.displayserver.xwindows.model.XGC;

public interface XLibRenderer extends Disposable {

    void polyLine(XGC gc, BYTE coordinateMode, POINT [] points);
    
}
