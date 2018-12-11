package com.neaterbits.displayserver.windows;

import java.util.Objects;

import com.neaterbits.displayserver.framebuffer.common.DisplayDevice;
import com.neaterbits.displayserver.framebuffer.common.FrameBuffer;

public class Output {

    private final ProviderEncoderConnector renderer;
    private final DisplayDevice displayDevice;
    private final FrameBuffer frameBuffer;

    public Output(Output output) {
        this(output.renderer, output.displayDevice, output.frameBuffer);
    }

    public Output(ProviderEncoderConnector renderer, DisplayDevice displayDevice, FrameBuffer frameBuffer) {

        Objects.requireNonNull(renderer);
        Objects.requireNonNull(displayDevice);
        Objects.requireNonNull(frameBuffer);
        
        this.renderer = renderer;
        this.displayDevice = displayDevice;
        this.frameBuffer = frameBuffer;
    }

    public ProviderEncoderConnector getRenderer() {
        return renderer;
    }
    
    public DisplayDevice getDisplayDevice() {
        return displayDevice;
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
