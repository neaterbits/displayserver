package com.neaterbits.displayserver.buffers;

public interface PixelConversion {

    int getRed(int pixel); 
    int getGreen(int pixel);
    int getBlue(int pixel);
    
    int getPixel(int red, int green, int blue);
}
