package com.neaterbits.displayserver.protocol.util;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public class XGCAttributesBuilder extends XAttributesBuilder {

    private BYTE function;
    
    private CARD32 planeMask;
    private CARD32 foreground;
    private CARD32 background;
    
    private CARD16 lineWidth;
    
    private BYTE lineStyle;
    private BYTE capStyle;
    private BYTE joinStyle;
    private BYTE fillStyle;
    private BYTE fillRule;
    
    private PIXMAP tile;
    private PIXMAP stipple;
    
    private INT16 tileStippleXOrigin;
    private INT16 tileStippleYOrigin;
    
    private FONT font;
    
    private BYTE subwindowMode;
    
    private BOOL graphicsExposures;
    
    private INT16 clipXOrigin;
    private INT16 clipYOrigin;
    
    private PIXMAP clipMask;
    
    private CARD16 dashOffset;
    
    private CARD8 dashes;
    
    private BYTE arcMode;

    public XGCAttributesBuilder addFunction(BYTE function) {

        Objects.requireNonNull(function);
        
        addFlag(XGCAttributes.FUNCTION);
        
        this.function = function;
    
        return this;
    }
    
    public XGCAttributesBuilder setFont(FONT font) {
        
        this.font = set(font, XGCAttributes.FONT);
        
        return this;
    }
    
    public XGCAttributes build() {
        
        return new XGCAttributes(
                getBitmask(),
                function,
                planeMask,
                foreground,
                background,
                lineWidth,
                lineStyle,
                capStyle,
                joinStyle,
                fillStyle,
                fillRule,
                tile,
                stipple,
                tileStippleXOrigin,
                tileStippleYOrigin,
                font,
                subwindowMode,
                graphicsExposures,
                clipXOrigin,
                clipYOrigin,
                clipMask,
                dashOffset,
                dashes,
                arcMode);
        
    }
}
