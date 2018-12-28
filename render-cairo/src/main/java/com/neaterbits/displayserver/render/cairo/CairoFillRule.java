package com.neaterbits.displayserver.render.cairo;

public enum CairoFillRule {
    WINDING(0),
    EVEN_ODD(1);

    private final int cairoFillRule;

    private CairoFillRule(int cairoFillRule) {
        this.cairoFillRule = cairoFillRule;
    }

    int getCairoFillRule() {
        return cairoFillRule;
    }
}

