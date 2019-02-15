package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public final class SetDashes extends XRequest {

    private final GCONTEXT gc;
    private final CARD16 dashOffset;
    private final CARD8 [] dashes;
    
    public static SetDashes decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final GCONTEXT gc = stream.readGCONTEXT();
        final CARD16 dashOffset = stream.readCARD16();
        
        final CARD16 lengthOfDashes = stream.readCARD16();
        
        final int length = lengthOfDashes.getValue();
        
        final CARD8 [] dashes = decodeArray(stream, new CARD8[length], XWindowsProtocolInputStream::readCARD8);
        
        final int padding = XWindowsProtocolUtil.getPadding(length);
        
        stream.readPad(padding);
        
        return new SetDashes(gc, dashOffset, dashes);
    }
    
    
    public SetDashes(GCONTEXT gc, CARD16 dashOffset, CARD8[] dashes) {

        Objects.requireNonNull(gc);
        Objects.requireNonNull(dashOffset);
        Objects.requireNonNull(dashes);
        
        this.gc = gc;
        this.dashOffset = dashOffset;
        this.dashes = dashes;
    }

    public GCONTEXT getGc() {
        return gc;
    }

    public CARD16 getDashOffset() {
        return dashOffset;
    }

    public CARD8[] getDashes() {
        return dashes;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "gc", gc,
                "dashOffset", dashOffset,
                "dashes", Arrays.toString(dashes)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(dashes.length);
        
        writeRequestLength(stream, 3 + (dashes.length + padding) / 4);
        
        stream.writeGCONTEXT(gc);
        stream.writeCARD16(dashOffset);

        stream.writeCARD16(new CARD16(dashes.length));
        
        encodeArray(dashes, stream);
    
        stream.pad(padding);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SET_DASHES;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
