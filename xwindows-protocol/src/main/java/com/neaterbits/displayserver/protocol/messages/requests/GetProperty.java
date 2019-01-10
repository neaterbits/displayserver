package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.GetPropertyReply;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GetProperty extends Request {

    private final BOOL delete;
    private final WINDOW window;
    private final ATOM property;
    private final ATOM type;
    private final CARD32 longOffset;
    private final CARD32 longLength;
    
    public static GetProperty decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL delete = stream.readBOOL();
        
        stream.readCARD16();
        
        return new GetProperty(
                delete,
                stream.readWINDOW(),
                stream.readATOM(),
                stream.readATOM(),
                stream.readCARD32(),
                stream.readCARD32());
    }
    
    public GetProperty(BOOL delete, WINDOW window, ATOM property, ATOM type, CARD32 longOffset, CARD32 longLength) {
        this.delete = delete;
        this.window = window;
        this.property = property;
        this.type = type;
        this.longOffset = longOffset;
        this.longLength = longLength;
    }
    
    public BOOL getDelete() {
        return delete;
    }

    public WINDOW getWindow() {
        return window;
    }

    public ATOM getProperty() {
        return property;
    }

    public ATOM getType() {
        return type;
    }

    public CARD32 getLongOffset() {
        return longOffset;
    }

    public CARD32 getLongLength() {
        return longLength;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "delete", delete.isSet(),
                "window", window,
                "property", property,
                "type", type,
                "longOffset", longOffset,
                "longLength", longLength);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);

        stream.writeBOOL(delete);
        stream.writeCARD16(new CARD16(6));

        stream.writeWINDOW(window);
        stream.writeATOM(property);
        stream.writeATOM(type);
        stream.writeCARD32(longOffset);
        stream.writeCARD32(longLength);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_PROPERTY;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return GetPropertyReply.class;
    }
}
