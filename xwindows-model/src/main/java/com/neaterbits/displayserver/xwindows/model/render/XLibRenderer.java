package com.neaterbits.displayserver.xwindows.model.render;

import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.util.Disposable;
import com.neaterbits.displayserver.xwindows.model.XGC;

public interface XLibRenderer extends Disposable {

    void fillRectangle(int x, int y, int width, int height, int r, int g, int b);
    
    void polyLine(XGC gc, BYTE coordinateMode, POINT [] points);
    
    void flush();
}
