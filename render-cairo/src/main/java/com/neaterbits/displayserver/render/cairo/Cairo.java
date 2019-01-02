package com.neaterbits.displayserver.render.cairo;

public class Cairo extends CairoReference {

    public Cairo(CairoSurface surface) {
        super(CairoNative.cairo_create(surface.getCairoReference()));
    }

    public void setOperator(CairoOperator operator) {
        CairoNative.cairo_set_operator(getCairoReference(), operator.getCairoValue());
    }
    
    public void setSourceRGB(double red, double green, double blue) {
        CairoNative.cairo_set_source_rgb(getCairoReference(), red, green, blue);
    }
    
    public void setSourceSurface(CairoSurface surface, double x, double y) {
        CairoNative.cairo_set_source_surface(getCairoReference(), surface.getCairoReference(), x, y);
    }
    
    public void setFillRule(CairoFillRule fillRule) {
        CairoNative.cairo_set_fill_rule(getCairoReference(), fillRule.getCairoFillRule());
    }
    
    public void clip() {
        CairoNative.cairo_clip(getCairoReference());
    }
    
    public void fill() {
        CairoNative.cairo_fill(getCairoReference());
    }
    
    public void maskSurface(CairoSurface surface, double surfaceX, double surfaceY) {
        CairoNative.cairo_mask_surface(getCairoReference(), surface.getCairoReference(), surfaceX, surfaceY);
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
    
    // Path
    
    public void newPath() {
        CairoNative.cairo_new_path(getCairoReference());
    }
    
    public void moveTo(double x, double y) {
        CairoNative.cairo_move_to(getCairoReference(), x, y);
    }
    
    public void relMoveTo(double dx, double dy) {
        CairoNative.cairo_rel_move_to(getCairoReference(), dx, dy);
    }
    
    public void lineTo(double x, double y) {
        CairoNative.cairo_line_to(getCairoReference(), x, y);
    }
    
    public void relLineTo(double dx, double dy) {
        CairoNative.cairo_rel_line_to(getCairoReference(), dx, dy);
    }
    
    public void rectangle(double x, double y, double width, double height) {
        CairoNative.cairo_rectangle(getCairoReference(), x, y, width, height);
    }
    
    @Override
    public void dispose() {
        CairoNative.cairo_destroy(getCairoReference());
    }
}
