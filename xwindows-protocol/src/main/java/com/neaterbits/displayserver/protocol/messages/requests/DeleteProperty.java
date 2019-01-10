package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class DeleteProperty extends Request {

    private final WINDOW window;
    private final ATOM property;
    
    public static DeleteProperty decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new DeleteProperty(
                stream.readWINDOW(),
                stream.readATOM());
    }
    
    public DeleteProperty(WINDOW window, ATOM property) {

        Objects.requireNonNull(window);
        Objects.requireNonNull(property);
        
        this.window = window;
        this.property = property;
    }

    public WINDOW getWindow() {
        return window;
    }

    public ATOM getProperty() {
        return property;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("window", window, "property", property);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3);
        
        stream.writeWINDOW(window);
        stream.writeATOM(property);
    }

    @Override
    public int getOpCode() {
        return OpCodes.DELETE_PROPERTY;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
