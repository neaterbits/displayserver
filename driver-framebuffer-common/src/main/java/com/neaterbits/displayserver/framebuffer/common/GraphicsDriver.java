package com.neaterbits.displayserver.framebuffer.common;

import java.util.List;
import java.util.stream.Collectors;

public interface GraphicsDriver {

    List<RenderingProvider> getRenderingProviders();
    
    default List<Encoder> getEncoders() {
        return getRenderingProviders().stream()
                .flatMap(renderingProvider -> renderingProvider.getEncoders().stream())
                .collect(Collectors.toList());
    }

    default List<OutputConnector> getOutputConnectors() {
        return getRenderingProviders().stream()
                .flatMap(renderingProvider -> renderingProvider.getOutputConnectors().stream())
                .collect(Collectors.toList());
    }

    List<DisplayDevice> getDisplayDevices();
    
}
