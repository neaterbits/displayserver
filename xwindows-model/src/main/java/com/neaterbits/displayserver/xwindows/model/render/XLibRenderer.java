package com.neaterbits.displayserver.xwindows.model.render;

import java.util.function.Function;

import com.neaterbits.displayserver.buffers.Buffer;
import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.types.SEGMENT;
import com.neaterbits.displayserver.util.Disposable;
import com.neaterbits.displayserver.xwindows.model.XGC;

public interface XLibRenderer extends Disposable {

    public static <T> T getGCValue(XGC gc, int flag, Function<XGCAttributes, T> getValue) {
        
        final T value;
        
        if (gc.getAttributes().isSet(flag)) {
            value = getValue.apply(gc.getAttributes());
        }
        else {
            if (!XGCAttributes.DEFAULT_ATTRIBUTES.isSet(flag)) {
                throw new IllegalArgumentException();
            }
            
            value = getValue.apply(XGCAttributes.DEFAULT_ATTRIBUTES);
        }
        
        
        return value;
    }
    
    void fillRectangle(int x, int y, int width, int height, int r, int g, int b);
    
    void polyPoint(XGC gc, BYTE coordinateMode, POINT [] points);

    void polyLine(XGC gc, BYTE coordinateMode, POINT [] points);
    
    void polySegment(XGC gc, SEGMENT [] segments);
    
    void polyFillRectangle(XGC gc, RECTANGLE [] rectangles);
    
    void polyRectangle(XGC gc, RECTANGLE [] rectangles);

    void fillPoly(XGC gc, POINT [] points);

    void putImage(XGC gc, int format, int width, int height, int dstX, int dstY, int leftPad, int depth, byte [] data);

    void renderBitmap(XGC gc, Buffer buffer, int x, int y);
    
    void flush();
}
