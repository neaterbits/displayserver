package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class ChangeWindowAttributes extends Request {

    private final WINDOW window;
    private final WindowAttributes attributes;

    public static ChangeWindowAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new ChangeWindowAttributes(stream.readWINDOW(), WindowAttributes.decode(stream));
    }
    
    public ChangeWindowAttributes(WINDOW window, WindowAttributes attributes) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(attributes);
    
        this.window = window;
        this.attributes = attributes;
    }

    public WINDOW getWindow() {
        return window;
    }

    public WindowAttributes getAttributes() {
        return attributes;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap("window", window, "attributes", LogUtil.outputParametersInBrackets(attributes.getDebugParams()));
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3 + attributes.getCount());
        
        attributes.encode(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CHANGE_WINDOW_ATTRIBUTES;
    }
}
