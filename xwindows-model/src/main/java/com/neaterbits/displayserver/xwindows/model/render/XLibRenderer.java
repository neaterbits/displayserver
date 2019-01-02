package com.neaterbits.displayserver.xwindows.model.render;

import java.util.function.Function;

import com.neaterbits.displayserver.buffers.Buffer;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.util.Disposable;
import com.neaterbits.displayserver.xwindows.model.XGC;

public interface XLibRenderer extends Disposable {

    public static <T> T getGCValue(XGC gc, int flag, Function<GCAttributes, T> getValue) {
        
        final T value;
        
        if (gc.getAttributes().isSet(flag)) {
            value = getValue.apply(gc.getAttributes());
        }
        else {
            if (!GCAttributes.DEFAULT_ATTRIBUTES.isSet(flag)) {
                throw new IllegalArgumentException();
            }
            
            value = getValue.apply(GCAttributes.DEFAULT_ATTRIBUTES);
        }
        
        
        return value;
    }
    
    void fillRectangle(int x, int y, int width, int height, int r, int g, int b);
    
    void polyLine(XGC gc, BYTE coordinateMode, POINT [] points);
    
    void renderBitmap(XGC gc, Buffer buffer, int x, int y);
    
    void flush();
}
