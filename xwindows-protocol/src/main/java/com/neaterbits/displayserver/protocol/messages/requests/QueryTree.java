package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class QueryTree extends Request {

    private final WINDOW window;

    public static QueryTree decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
    
        return new QueryTree(stream.readWINDOW());
    }
    
    public QueryTree(WINDOW window) {
        
        Objects.requireNonNull(window);
        
        this.window = window;
    }

    public WINDOW getWindow() {
        return window;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("window", window);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeWINDOW(window);
    }

    @Override
    public int getOpCode() {
        return OpCodes.QUERY_TREE;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        throw new UnsupportedOperationException("TODO");
    }
}
