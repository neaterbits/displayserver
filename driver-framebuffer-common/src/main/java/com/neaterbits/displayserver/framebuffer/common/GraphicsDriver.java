package com.neaterbits.displayserver.framebuffer.common;

import java.util.List;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.driver.common.Driver;

public interface GraphicsDriver extends Driver {

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
