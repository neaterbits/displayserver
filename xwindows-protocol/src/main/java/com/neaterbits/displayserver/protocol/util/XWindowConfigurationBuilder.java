package com.neaterbits.displayserver.protocol.util;

import com.neaterbits.displayserver.protocol.messages.requests.XWindowConfiguration;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class XWindowConfigurationBuilder extends XAttributesBuilder {

    private INT16 x;
    private INT16 y;
    private CARD16 width;
    private CARD16 height;
    private CARD16 borderWidth;
    private WINDOW sibling;
    private BYTE stackMode;
    
    public XWindowConfigurationBuilder setX(INT16 x) {
        this.x = set(x, XWindowConfiguration.X);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setY(INT16 y) {
        this.y = set(y, XWindowConfiguration.Y);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setWidth(CARD16 width) {
        this.width = set(width, XWindowConfiguration.WIDTH);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setHeight(CARD16 height) {
        this.height = set(height, XWindowConfiguration.HEIGHT);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setBorderWidth(CARD16 borderWidth) {
        this.borderWidth = set(borderWidth, XWindowConfiguration.BORDER_WIDTH);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setSibling(WINDOW sibling) {
        this.sibling = set(sibling, XWindowConfiguration.SIBLING);
        
        return this;
    }
    
    public XWindowConfigurationBuilder setStackMode(BYTE stackMode) {
        this.stackMode = set(stackMode, XWindowConfiguration.STACK_MODE);
        
        return this;
    }

    public XWindowConfiguration build() {
        return new XWindowConfiguration(getBitmask(), x, y, width, height, borderWidth, sibling, stackMode);
    }
}
