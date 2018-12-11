package com.neaterbits.displayserver.framebuffer.common;

/**
 * Corresponds to Linux DRM encoder 
 * 
 */

public interface Encoder {

    boolean mayOutputTo(DisplayDevice displayDevice);
    
    boolean supports(OutputConnector connector);
}
