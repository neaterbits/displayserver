package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.util.XGCAttributesBuilder;

public final class XGC {

    private XGCAttributes attributes;

    private RECTANGLE [] clipRectangles;
    
    public XGC(XGCAttributes attributes) {

        Objects.requireNonNull(attributes);
        
        this.attributes = attributes;
    }

    public XGCAttributes getAttributes() {
        return attributes;
    }
    
    public void setAttributes(XGCAttributes attributes) {
        this.attributes = attributes;
    }

    public void setFont(FONT font) {
 
        Objects.requireNonNull(font);
        
        final XGCAttributes updatedAttributes = new XGCAttributesBuilder()
                .setFont(font)
                .build();
        
        this.attributes = attributes.applyImmutably(updatedAttributes);
    }

    public INT16 getClipXOrigin() {
        return attributes.getClipXOrigin();
    }

    public INT16 getClipYOrigin() {
        return attributes.getClipYOrigin();
    }

    public RECTANGLE[] getClipRectangles() {
        return clipRectangles;
    }

    public void setClipRectangles(INT16 clipXOrigin, INT16 clipYOrigin, RECTANGLE[] clipRectangles) {
        
        Objects.requireNonNull(clipXOrigin);
        Objects.requireNonNull(clipYOrigin);
        Objects.requireNonNull(clipRectangles);

        final XGCAttributes updatedAttributes = new XGCAttributesBuilder()
                .setClipXOrigin(clipXOrigin)
                .setClipYOrigin(clipYOrigin)
                .build();
        
        this.attributes = attributes.applyImmutably(updatedAttributes);
        this.clipRectangles = clipRectangles;
    }
}
