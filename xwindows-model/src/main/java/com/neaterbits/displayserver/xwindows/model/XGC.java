package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.util.XGCAttributesBuilder;
import com.neaterbits.displayserver.util.ArrayUtil;

public final class XGC {

    private XGCAttributes attributes;

    private CARD8 [] dashes;

    private RECTANGLE [] clipRectangles;
    
    public XGC(XGCAttributes attributes) {

        Objects.requireNonNull(attributes);

        setAttributes(attributes);
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

    public CARD8[] getDashes() {
        return dashes;
    }

    public void setDashes(CARD16 dashOffset, CARD8[] dashes) {

        Objects.requireNonNull(dashOffset);
        Objects.requireNonNull(dashes);
        
        final XGCAttributes updatedAttributes = new XGCAttributesBuilder()
                .setDashOffset(dashOffset)
                .build();
        
        this.attributes = attributes.applyImmutably(updatedAttributes);

        if (dashes.length % 2 == 1) {
            this.dashes = ArrayUtil.merge(dashes, dashes, CARD8[]::new);
        }
        else {
            this.dashes = dashes;
        }
    }

    public void setClipRectangles(RECTANGLE[] clipRectangles) {
        this.clipRectangles = clipRectangles;
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
