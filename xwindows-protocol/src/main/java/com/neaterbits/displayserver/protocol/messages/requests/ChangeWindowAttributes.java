package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class ChangeWindowAttributes extends XRequest {

    private final WINDOW window;
    private final XWindowAttributes attributes;

    public static ChangeWindowAttributes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final ChangeWindowAttributes changeWindowAttributes = new ChangeWindowAttributes(stream.readWINDOW(), XWindowAttributes.decode(stream));
        
        return changeWindowAttributes;
    }
    
    public ChangeWindowAttributes(WINDOW window, XWindowAttributes attributes) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(attributes);
    
        this.window = window;
        this.attributes = attributes;
    }

    public WINDOW getWindow() {
        return window;
    }

    public XWindowAttributes getAttributes() {
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
        
        System.out.println("## write attributes " + attributes.getCount());
        
        writeRequestLength(stream, 3 + attributes.getCount());
        
        stream.writeWINDOW(window);
        
        attributes.encode(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CHANGE_WINDOW_ATTRIBUTES;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
