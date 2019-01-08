package com.neaterbits.displayserver.windows.compositor;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.Cairo;
import com.neaterbits.displayserver.render.cairo.CairoStatus;
import com.neaterbits.displayserver.render.cairo.CairoSurface;

final class CairoSurfaceWrapper implements CairoSurface {

    private CairoSurface cairoSurface;
    private CoordinateTranslator coordinateTranslator;
    
    CairoSurfaceWrapper(CairoSurface cairoSurface, CoordinateTranslator coordinateTranslator) {
        
        Objects.requireNonNull(cairoSurface);
        Objects.requireNonNull(coordinateTranslator);
        
        if (cairoSurface instanceof CairoSurfaceWrapper) {
            throw new IllegalArgumentException();
        }
        
        this.cairoSurface = cairoSurface;
        this.coordinateTranslator = coordinateTranslator;
    }

    CairoSurface getCairoSurface() {
        return cairoSurface;
    }

    CoordinateTranslator getCoordinateTranslator() {
        return coordinateTranslator;
    }

    void setCoordinateTranslator(CoordinateTranslator coordinateTranslator) {
        this.coordinateTranslator = coordinateTranslator;
    }

    @Override
    public Cairo createContext() {
        return new CairoWrapper(cairoSurface.createContext(), this);
    }

    @Override
    public void flush() {
        cairoSurface.flush();
    }
    
    @Override
    public CairoStatus writeToPNG(String fileName) {
        return cairoSurface.writeToPNG(fileName);
    }

    @Override
    public void dispose() {
        cairoSurface.dispose();
    }

    @Override
    public String toString() {
        return "CairoSurfaceWrapper [cairoSurface=" + cairoSurface + ", coordinateTranslator=" + coordinateTranslator
                + "]";
    }
}
