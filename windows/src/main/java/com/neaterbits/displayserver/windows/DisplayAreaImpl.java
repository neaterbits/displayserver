package com.neaterbits.displayserver.windows;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.framebuffer.common.OffscreenBufferProvider;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.NoopCoordinateTranslator;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;

public final class DisplayAreaImpl implements DisplayArea {

    private final DisplayAreaConfig config;
    private final Size size;
    private final Size sizeInMillimeters;
    private final int depth;
    private final PixelFormat pixelFormat;
    private final OffscreenBufferProvider offscreenBufferProvider;
    private final List<ViewPort> viewPorts;
    
    public DisplayAreaConfig getConfig() {
        return config;
    }

    DisplayAreaImpl(
            DisplayAreaConfig config,
            Size size,
            Size sizeInMillimeters,
            int depth,
            PixelFormat pixelFormat,
            List<ViewPort> viewPorts,
            OffscreenBufferProvider offscreenBufferProvider) {
        
        Objects.requireNonNull(config);
        Objects.requireNonNull(size);
        Objects.requireNonNull(sizeInMillimeters);
        Objects.requireNonNull(pixelFormat);
        Objects.requireNonNull(viewPorts);
        Objects.requireNonNull(offscreenBufferProvider);
        
        if (viewPorts.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        this.config = config;
        this.size = size;
        this.sizeInMillimeters = sizeInMillimeters;
        this.depth = depth;
        this.pixelFormat = pixelFormat;
        this.viewPorts = viewPorts;
        this.offscreenBufferProvider = offscreenBufferProvider;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public Size getSizeInMillimeters() {
        return sizeInMillimeters;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    @Override
    public List<ViewPort> getViewPorts() {
        return Collections.unmodifiableList(viewPorts);
    }

    @Override
    public OffscreenSurface allocateOffscreenSurface(Size size, PixelFormat pixelFormat) {

        return new OffscreenSurfaceWrapper(
                offscreenBufferProvider.allocateOffscreenBuffer(size, pixelFormat),
                new NoopCoordinateTranslator(),
                size,
                pixelFormat.getDepth());
    }

    @Override
    public void freeOffscreenSurface(OffscreenSurface surface) {

        final OffscreenSurfaceWrapper offscreenSurfaceWrapper = (OffscreenSurfaceWrapper)surface;
        
        offscreenBufferProvider.freeOffscreenBuffer(offscreenSurfaceWrapper.getOffscreenBuffer());
    }

    @Override
    public boolean sameAs(DisplayArea other) {
        return this == other;
    }
}
