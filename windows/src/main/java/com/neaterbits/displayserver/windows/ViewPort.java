package com.neaterbits.displayserver.windows;

import java.util.Objects;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

/**
 * Mapping a display device into a DisplayArea, with positioning in display area
 *
 */

public final class ViewPort extends Output {

    private final Position position;
    private final Size size;
    private final Size sizeInMillimeters;

    ViewPort(
            Output output,
            Position position,
            Size size,
            Size sizeInMillimeters) {
        
        super(output);

        Objects.requireNonNull(position);
        Objects.requireNonNull(size);
        Objects.requireNonNull(sizeInMillimeters);
        
        this.position = position;
        this.size = size;
        this.sizeInMillimeters = sizeInMillimeters;
        
    }

    Position getPosition() {
        return position;
    }

    Size getSize() {
        return size;
    }

    Size getSizeInMillimeters() {
        return sizeInMillimeters;
    }
}
