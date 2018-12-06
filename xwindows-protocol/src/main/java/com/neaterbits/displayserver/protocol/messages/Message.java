package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.util.logging.LogUtil;

public abstract class Message implements Encodeable {
    
    public Object [] getDebugParams() {
        return null;
    }
    
    protected final Object [] wrap(Object ... objects) {
        return LogUtil.wrap(objects);
    }
    
    public final String toDebugString() {
        
        final StringBuilder sb = new StringBuilder();
        
        sb.append(getClass().getSimpleName());
        
        final Object [] debugParams = getDebugParams();
        
        if (debugParams != null) {
            sb.append(" [");
            
            LogUtil.outputParameters(sb::append, debugParams);
            
            sb.append(']');
        }
        
        return sb.toString();
    }
    
    protected static void writeUnusedByte(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)0));
    }

    protected static void readUnusedByte(XWindowsProtocolInputStream stream) throws IOException {
        stream.readBYTE();
    }

    protected static void writeUnusedCARD16(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD16(new CARD16(0));
    }
}
