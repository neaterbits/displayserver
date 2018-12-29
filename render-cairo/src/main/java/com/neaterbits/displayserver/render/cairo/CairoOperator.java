package com.neaterbits.displayserver.render.cairo;

public enum CairoOperator {

    CLEAR,
    SOURCE,
    OVER,
    IN,
    OUT,
    ATOP,
    DEST,
    DEST_OVER,
    DEST_IN,
    DEST_OUT,
    DEST_ATOP,
    XOR,
    ADD,
    SATURATE;
    
    private final int cairoValue;
    
    private CairoOperator() {
        this.cairoValue = CairoNative.get_cairo_operator_value("CAIRO_OPERATOR_" + name());
        
        if (cairoValue < 0 || cairoValue >= CairoOperator.values().length) {
            throw new IllegalStateException();
        }
    }

    int getCairoValue() {
        return cairoValue;
    }
}
