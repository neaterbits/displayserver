package com.neaterbits.displayserver.framebuffer.xwindows;

import java.util.Objects;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

final class XTestDisplay {

    private final Position position;
    private final Size size;
    
    XTestDisplay(Position position, Size size) {
        
        Objects.requireNonNull(position);
        Objects.requireNonNull(size);
        
        this.position = position;
        this.size = size;
    }

    public Position getPosition() {
        return position;
    }

    public Size getSize() {
        return size;
    }
}
