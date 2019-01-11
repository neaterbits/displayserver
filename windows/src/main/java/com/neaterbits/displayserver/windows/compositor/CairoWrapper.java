package com.neaterbits.displayserver.windows.compositor;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.Cairo;
import com.neaterbits.displayserver.render.cairo.CairoFillRule;
import com.neaterbits.displayserver.render.cairo.CairoOperator;
import com.neaterbits.displayserver.render.cairo.CairoStatus;
import com.neaterbits.displayserver.render.cairo.CairoSurface;

final class CairoWrapper implements Cairo {

    private Cairo cr;
    private CairoSurfaceWrapper cairoSurface;

    CairoWrapper(Cairo cr, CairoSurfaceWrapper cairoSurface) {
        
        Objects.requireNonNull(cr);
        Objects.requireNonNull(cairoSurface);
     
        if (cr instanceof CairoWrapper) {
            throw new IllegalArgumentException();
        }
        
        this.cr = cr;
        this.cairoSurface = cairoSurface;
    }

    
    @Override
    public CairoStatus getStatus() {
        return cr.getStatus();
    }

    @Override
    public void setOperator(CairoOperator operator) {
        cr.setOperator(operator);
    }

    @Override
    public void setSourceRGB(double red, double green, double blue) {
        cr.setSourceRGB(red, green, blue);
    }

    @Override
    public void setSourceSurface(CairoSurface surface, double x, double y) {
        if (surface instanceof CairoSurfaceWrapper) {
            surface = ((CairoSurfaceWrapper)surface).getCairoSurface();
        }

        cr.setSourceSurface(
                surface,
                cairoSurface.getCoordinateTranslator().translateX(x),
                cairoSurface.getCoordinateTranslator().translateY(y));
    }

    @Override
    public void setFillRule(CairoFillRule fillRule) {
        cr.setFillRule(fillRule);
    }

    @Override
    public void clip() {
        cr.clip();
    }

    @Override
    public void fill() {
        cr.fill();
    }

    @Override
    public void maskSurface(CairoSurface surface, double surfaceX, double surfaceY) {
        
        if (!(surface instanceof CairoSurfaceWrapper)) {
            throw new IllegalArgumentException();
        }
        
        cr.maskSurface(
                ((CairoSurfaceWrapper)surface).getCairoSurface(),
                cairoSurface.getCoordinateTranslator().translateX(surfaceX),
                cairoSurface.getCoordinateTranslator().translateY(surfaceY));
    }

    @Override
    public void paint() {
        cr.paint();
    }

    @Override
    public void stroke() {
        cr.stroke();
    }

    @Override
    public void strokePreserve() {
        cr.strokePreserve();
    }

    @Override
    public void newPath() {
        cr.newPath();
    }

    @Override
    public void moveTo(double x, double y) {
        cr.moveTo(
                cairoSurface.getCoordinateTranslator().translateX(x),
                cairoSurface.getCoordinateTranslator().translateY(y));
    }

    @Override
    public void relMoveTo(double dx, double dy) {
        cr.relMoveTo(dx, dy);
    }

    @Override
    public void lineTo(double x, double y) {
        cr.lineTo(
                cairoSurface.getCoordinateTranslator().translateX(x),
                cairoSurface.getCoordinateTranslator().translateY(y));
    }

    @Override
    public void relLineTo(double dx, double dy) {
        cr.relLineTo(dx, dy);
    }

    @Override
    public void rectangle(double x, double y, double width, double height) {
        cr.rectangle(
                cairoSurface.getCoordinateTranslator().translateX(x),
                cairoSurface.getCoordinateTranslator().translateY(y),
                width,
                height);
    }
    
    @Override
    public void dispose() {
        cr.dispose();
    }
}
