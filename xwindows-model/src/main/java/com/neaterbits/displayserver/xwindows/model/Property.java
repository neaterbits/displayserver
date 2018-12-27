package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class Property {
    private final ATOM property;
    private final ATOM type;
    private final CARD8 format;
    private final byte [] data;
    
    Property(ATOM property, ATOM type, CARD8 format, byte[] data) {
        
        Objects.requireNonNull(property);
        Objects.requireNonNull(type);
        Objects.requireNonNull(format);
        Objects.requireNonNull(data);
        
        this.property = property;
        this.type = type;
        this.format = format;
        this.data = data;
    }

    public ATOM getProperty() {
        return property;
    }

    public ATOM getType() {
        return type;
    }

    public CARD8 getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }
}
