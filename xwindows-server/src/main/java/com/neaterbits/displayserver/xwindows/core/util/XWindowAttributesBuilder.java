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


    private <T> T set(T value, int flag) {
        
        Objects.requireNonNull(value);
        
        addFlag(flag);
        
        return value;
    }
    
    public XWindowAttributesBuilder setBackgroundPixmap(PIXMAP pixmap) {
        
        this.backgroundPixmap = set(pixmap, XWindowAttributes.BACKGROUND_PIXMAP);

        return this;
    }
    
    public XWindowAttributesBuilder setBackgroundPixel(long backgroundPixel) {
        
        this.backgroundPixel = set(new CARD32(backgroundPixel), XWindowAttributes.BACKGROUND_PIXEL);
    
        return this;
    }

    public XWindowAttributesBuilder setBorderPixmap(PIXMAP pixmap) {
        
        this.borderPixmap = set(pixmap, XWindowAttributes.BORDER_PIXMAP);

        return this;
    }
    
    public XWindowAttributesBuilder setBorderPixel(long borderPixel) {
        
        this.borderPixel = set(new CARD32(borderPixel), XWindowAttributes.BORDER_PIXEL);
    
        return this;
    }

    public XWindowAttributesBuilder setBitGravity(BITGRAVITY bitGravity) {
        
        this.bitGravity = set(bitGravity, XWindowAttributes.BIT_GRAVITY);
    
        return this;
    }

    public XWindowAttributesBuilder setBitGravity(WINGRAVITY winGravity) {
        
        this.winGravity = set(winGravity, XWindowAttributes.WIN_GRAVITY);
    
        return this;
    }

    public XWindowAttributesBuilder setBackingStore(BYTE backingStore) {
        
        this.backingStore = set(backingStore, XWindowAttributes.BACKING_STORE);
    
        return this;
    }

    public XWindowAttributesBuilder setBackingPlanes(int backingPlanes) {
        
        this.backingPlanes = set(new CARD32(backingPlanes), XWindowAttributes.BACKING_PLANES);
    
        return this;
    }

    public XWindowAttributesBuilder setBackingPixel(int backingPixel) {
        
        this.backingPixel = set(new CARD32(backingPixel), XWindowAttributes.BACKING_PIXEL);
    
        return this;
    }

    public XWindowAttributesBuilder setOverrideRedirect(boolean value) {
        return setOverrideRedirect(BOOL.valueOf(value));
    }
        
    public XWindowAttributesBuilder setOverrideRedirect(BOOL value) {
        
        this.overrideRedirect = set(value, XWindowAttributes.OVERRIDE_REDIRECT);
        
        return this;
    }

    public XWindowAttributesBuilder setSaveUnder(boolean value) {
        return setSaveUnder(BOOL.valueOf(value));
    }
        
    public XWindowAttributesBuilder setSaveUnder(BOOL value) {
        
        this.saveUnder = set(value, XWindowAttributes.SAVE_UNDER);
        
        return this;
    }

    public XWindowAttributesBuilder setEventMask(int eventMask) {
        
        this.eventMask = set(new SETofEVENT(eventMask), XWindowAttributes.EVENT_MASK);
        
        return this;
    }

    public XWindowAttributesBuilder setDoNotPropagateMask(int doNotPropagateMask) {
        
        this.doNotPropagateMask = set(new SETofDEVICEEVENT(doNotPropagateMask), XWindowAttributes.DO_NOT_PROPAGATE_MASK);
        
        return this;
    }

    public XWindowAttributesBuilder setColormap(COLORMAP colormap) {
        
        this.colormap = set(colormap, XWindowAttributes.COLOR_MAP);
        
        return this;
    }
    
    public XWindowAttributesBuilder setCursor(CURSOR cursor) {
        
        this.cursor = set(cursor, XWindowAttributes.CURSOR);
        
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
                backingPixel,
                backingPixel,
                overrideRedirect,
                saveUnder,
                eventMask,
                doNotPropagateMask,
                colormap,
                cursor);
    }
}
