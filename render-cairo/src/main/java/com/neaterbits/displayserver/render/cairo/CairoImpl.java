package com.neaterbits.displayserver.render.cairo;

final class CairoImpl extends CairoReference implements Cairo {

    CairoImpl(CairoSurfaceImpl surface) {
        super(CairoNative.cairo_create(surface.getCairoReference()));
    }

    @Override
    public void setOperator(CairoOperator operator) {
        CairoNative.cairo_set_operator(getCairoReference(), operator.getCairoValue());
    }
    
    @Override
    public void setSourceRGB(double red, double green, double blue) {
        CairoNative.cairo_set_source_rgb(getCairoReference(), red, green, blue);
    }
    
    @Override
    public void setSourceSurface(CairoSurface surface, double x, double y) {
        
        final CairoSurfaceImpl surfaceImpl = (CairoSurfaceImpl)surface;
        
        CairoNative.cairo_set_source_surface(getCairoReference(), surfaceImpl.getCairoReference(), x, y);
    }
    
    @Override
    public void setFillRule(CairoFillRule fillRule) {
        CairoNative.cairo_set_fill_rule(getCairoReference(), fillRule.getCairoFillRule());
    }
    
    @Override
    public void clip() {
        CairoNative.cairo_clip(getCairoReference());
    }
    
    @Override
    public void fill() {
        CairoNative.cairo_fill(getCairoReference());
    }
    
    @Override
    public void maskSurface(CairoSurface surface, double surfaceX, double surfaceY) {
        final CairoSurfaceImpl surfaceImpl = (CairoSurfaceImpl)surface;

        CairoNative.cairo_mask_surface(getCairoReference(), surfaceImpl.getCairoReference(), surfaceX, surfaceY);
    }
    
    @Override
    public void paint() {
        CairoNative.cairo_paint(getCairoReference());
    }
    
    @Override
    public void stroke() {
        CairoNative.cairo_stroke(getCairoReference());
    }
    
    @Override
    public void strokePreserve() {
        CairoNative.cairo_stroke_preserve(getCairoReference());
    }
    
    // Path
    
    @Override
    public void newPath() {
        CairoNative.cairo_new_path(getCairoReference());
    }
    
    @Override
    public void moveTo(double x, double y) {
        CairoNative.cairo_move_to(getCairoReference(), x, y);
    }
    
    @Override
    public void relMoveTo(double dx, double dy) {
        CairoNative.cairo_rel_move_to(getCairoReference(), dx, dy);
    }
    
    @Override
    public void lineTo(double x, double y) {
        CairoNative.cairo_line_to(getCairoReference(), x, y);
    }
    
    @Override
    public void relLineTo(double dx, double dy) {
        CairoNative.cairo_rel_line_to(getCairoReference(), dx, dy);
    }
    
    @Override
    public void rectangle(double x, double y, double width, double height) {
        CairoNative.cairo_rectangle(getCairoReference(), x, y, width, height);
    }
    
    @Override
    public void dispose() {
        CairoNative.cairo_destroy(getCairoReference());
    }
}
