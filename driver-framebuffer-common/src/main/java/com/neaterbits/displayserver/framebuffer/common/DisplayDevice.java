package com.neaterbits.displayserver.framebuffer.common;

import java.util.List;

import com.neaterbits.displayserver.types.Size;

public interface DisplayDevice {

    DisplayDeviceId getDisplayDeviceId(); 
    
    Size getSizeInMillimeters();
    
    List<DisplayMode> getAvailableModes();
}
