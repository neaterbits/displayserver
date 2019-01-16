package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Objects;

final class XLFDAlias {

    private final XLFD from;
    private final XLFD to;
    
    XLFDAlias(XLFD from, XLFD to) {
        
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        
        this.from = from;
        this.to = to;
    }

    XLFD getFrom() {
        return from;
    }

    XLFD getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "XLFDAlias [from=" + from + ", to=" + to + "]";
    }
}
