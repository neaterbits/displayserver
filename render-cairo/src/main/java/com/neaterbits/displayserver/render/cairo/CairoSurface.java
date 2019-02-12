package com.neaterbits.displayserver.render.cairo;

import com.neaterbits.displayserver.util.Disposable;

public interface CairoSurface extends Disposable {
    
    void flush();

    Cairo createContext();

    CairoStatus writeToPNG(String fileName);

    int getWidth();
    
    int getHeight();
}
