package com.neaterbits.displayserver.framebuffer.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class BaseGraphicsDriver implements GraphicsDriver {

    private final List<RenderingProvider> renderingProviders;
    private final List<DisplayDevice> displayDevices;
    
    protected BaseGraphicsDriver() {
        this.renderingProviders = new ArrayList<>();
        this.displayDevices = new ArrayList<>();
    }
    
    protected final void addRenderingProvider(RenderingProvider renderingProvider) {
        
        Objects.requireNonNull(renderingProvider);
        
        renderingProviders.add(renderingProvider);
    }
    
    protected final void addDisplayDevice(DisplayDevice displayDevice) {
        
        Objects.requireNonNull(displayDevice);
        
        displayDevices.add(displayDevice);
    }
    
    @Override
    public final List<RenderingProvider> getRenderingProviders() {
        return Collections.unmodifiableList(renderingProviders);
    }

    @Override
    public final List<DisplayDevice> getDisplayDevices() {
        return Collections.unmodifiableList(displayDevices);
    }
}
