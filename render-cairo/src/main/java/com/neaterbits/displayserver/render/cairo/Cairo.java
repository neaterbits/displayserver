package com.neaterbits.displayserver.render.cairo;

import com.neaterbits.displayserver.util.Disposable;

public interface Cairo extends Disposable {
    
    CairoStatus getStatus();
    
    void setOperator(CairoOperator operator);
    
    void setSourceRGB(double red, double green, double blue);
    
    void setSourceSurface(CairoSurface surface, double x, double y);
    
    void setFillRule(CairoFillRule fillRule);
    
    void clip();
    
    void resetClip();
    
    void fill();
    
    void maskSurface(CairoSurface surface, double surfaceX, double surfaceY);
    
    void paint();
    
    void stroke();
    
    void strokePreserve();
    
    // Path
    void newPath();
    
    void moveTo(double x, double y);
    
    void relMoveTo(double dx, double dy);
    
    void lineTo(double x, double y);
    
    void relLineTo(double dx, double dy);
    
    void rectangle(double x, double y, double width, double height);
}
