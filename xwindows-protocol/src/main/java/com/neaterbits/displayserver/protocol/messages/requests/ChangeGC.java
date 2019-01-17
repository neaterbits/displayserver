package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class ChangeGC extends Request {

    private final GCONTEXT gc;
    private final XGCAttributes attributes;
    
    public ChangeGC(GCONTEXT gc, XGCAttributes attributes) {
        this.gc = gc;
        this.attributes = attributes;
    }

    public static ChangeGC decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new ChangeGC(stream.readGCONTEXT(), XGCAttributes.decode(stream));
    }
    
    public Object[] getDebugParams() {
        return wrap(
                "gc", gc,
                "attributes", LogUtil.outputParametersInBrackets(attributes.getDebugParams())
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 3 + attributes.getCount());
        
        stream.writeGCONTEXT(gc);
     
        attributes.encode(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CHANGE_GC;
    }

    public GCONTEXT getGc() {
        return gc;
    }

    public XGCAttributes getAttributes() {
        return attributes;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
