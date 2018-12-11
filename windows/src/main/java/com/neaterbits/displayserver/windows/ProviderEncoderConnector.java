package com.neaterbits.displayserver.windows;

import java.util.Objects;

import com.neaterbits.displayserver.framebuffer.common.Encoder;
import com.neaterbits.displayserver.framebuffer.common.OutputConnector;
import com.neaterbits.displayserver.framebuffer.common.RenderingProvider;

class ProviderEncoderConnector {

    private final RenderingProvider provider;
    private final Encoder encoder;
    private final OutputConnector connector;
    
    ProviderEncoderConnector(RenderingProvider provider, Encoder encoder, OutputConnector connector) {
        
        Objects.requireNonNull(provider);
        Objects.requireNonNull(encoder);
        Objects.requireNonNull(connector);
        
        this.provider = provider;
        this.encoder = encoder;
        this.connector = connector;
    }

    RenderingProvider getProvider() {
        return provider;
    }

    Encoder getEncoder() {
        return encoder;
    }

    OutputConnector getConnector() {
        return connector;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connector == null) ? 0 : connector.hashCode());
        result = prime * result + ((encoder == null) ? 0 : encoder.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProviderEncoderConnector other = (ProviderEncoderConnector) obj;
        if (connector == null) {
            if (other.connector != null)
                return false;
        } else if (!connector.equals(other.connector))
            return false;
        if (encoder == null) {
            if (other.encoder != null)
                return false;
        } else if (!encoder.equals(other.encoder))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        return true;
    }
}
