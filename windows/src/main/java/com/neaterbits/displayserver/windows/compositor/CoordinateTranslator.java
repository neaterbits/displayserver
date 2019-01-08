package com.neaterbits.displayserver.windows.compositor;

public interface CoordinateTranslator {

    int translateX(int x);
    
    int translateY(int y);
    
    double translateX(double x);
    
    double translateY(double y);
    
}
