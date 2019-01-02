package com.neaterbits.displayserver.render.cairo;

public enum CairoFormat {

    INVALID,
    ARGB32,
    RGB24,
    A8,
    A1,
    RGB16_565,
    RGB30;
    
    private final int cairoValue;
    
    private CairoFormat() {
        this.cairoValue = CairoNative.get_cairo_format_enum_value("CAIRO_FORMAT_" + name());
        
        if (cairoValue < -1) {
            throw new IllegalStateException();
        }
    }

    int getCairoValue() {
        return cairoValue;
    }

    public int strideForWidth(int width) {
        return CairoNative.cairo_format_stride_for_width(getCairoValue(), width);
    }
}
