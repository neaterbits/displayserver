package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.util.XGCAttributesBuilder;

public final class XGC {

    private XGCAttributes attributes;

    public XGC(XGCAttributes attributes) {

        Objects.requireNonNull(attributes);
        
        this.attributes = attributes;
    }

    public XGCAttributes getAttributes() {
        return attributes;
    }
    
    public void setFont(FONT font) {
 
        Objects.requireNonNull(font);
        
        final XGCAttributes updatedAttributes = new XGCAttributesBuilder()
                .setFont(font)
                .build();
        
        this.attributes = attributes.applyImmutably(updatedAttributes);
    }
}
