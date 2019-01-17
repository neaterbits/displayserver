package com.neaterbits.displayserver.xwindows.core.util;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public final class XWindowAttributesBuilder extends XAttributesBuilder {

    private PIXMAP backgroundPixmap;
    private CARD32 backgroundPixel;
    private PIXMAP borderPixmap;
    private CARD32 borderPixel;
    private BITGRAVITY bitGravity;
    private WINGRAVITY winGravity;
    private BYTE backingStore;
    private CARD32 backingPlanes;
    private CARD32 backingPixel;
    private BOOL overrideRedirect;
    private BOOL saveUnder;
    private SETofEVENT eventMask;
    private SETofDEVICEEVENT doNotPropagateMask;
    private COLORMAP colormap;
    private CURSOR cursor;

    public XWindowAttributesBuilder setBackgroundPixmap(PIXMAP pixmap) {
        
        Objects.requireNonNull(pixmap);
        
        addFlag(XWindowAttributes.BACKGROUND_PIXMAP);
        
        this.backgroundPixmap = pixmap;

        return this;
    }
    
    public XWindowAttributesBuilder setBackgroundPixel(long backgroundPixel) {
        
        addFlag(XWindowAttributes.BACKGROUND_PIXEL);
    
        this.backgroundPixel = new CARD32(backgroundPixel);
    
        return this;
    }
    
    public XWindowAttributes build() {
        
        return new XWindowAttributes(
                getBitmask(),
                backgroundPixmap,
                backgroundPixel,
                borderPixmap,
                borderPixel,
                bitGravity,
                winGravity,
                backingStore,
                backingPlanes,
                backingPixel,
                overrideRedirect,
                saveUnder,
                eventMask,
                doNotPropagateMask,
                colormap,
                cursor);
    }
}
