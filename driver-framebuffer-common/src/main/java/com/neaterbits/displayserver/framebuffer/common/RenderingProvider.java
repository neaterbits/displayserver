package com.neaterbits.displayserver.framebuffer.common;

import java.util.List;

public interface RenderingProvider extends OffscreenBufferProvider, FrameBufferProvider {

    List<Encoder> getEncoders();
    
    List<OutputConnector> getOutputConnectors();
    
    boolean supports(Encoder encoder);
    
    boolean supports(DisplayDevice device);
}
