package com.neaterbits.displayserver.render.cairo;

public class Cairo extends CairoReference {

    public Cairo(CairoSurface surface) {
        super(CairoNative.cairo_create(surface.getCairoReference()));
    }

    public void setSourceRGB(double red, double green, double blue) {
        CairoNative.cairo_set_source_rgb(getCairoReference(), red, green, blue);
    }
    
    public void setFillRule(CairoFillRule fillRule) {
        CairoNative.cairo_set_fill_rule(getCairoReference(), fillRule.getCairoFillRule());
    }
    
    public void fill() {
        CairoNative.cairo_fill(getCairoReference());
    }
    
    public void paint() {
        CairoNative.cairo_paint(getCairoReference());
    }
    
    public void stroke() {
        CairoNative.cairo_stroke(getCairoReference());
    }
    
    public void strokePreserve() {
        CairoNative.cairo_stroke_preserve(getCairoReference());
    }
    
    public void rectangle(double x, double y, double width, double height) {
        CairoNative.cairo_rectangle(getCairoReference(), x, y, width, height);
    }
    
    @Override
    public void dispose() {
        CairoNative.cairo_destroy(getCairoReference());
    }
}
