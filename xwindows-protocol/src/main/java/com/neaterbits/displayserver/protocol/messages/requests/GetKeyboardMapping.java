package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.KEYCODE;

public final class GetKeyboardMapping extends XRequest {

    private final KEYCODE firstKeycode;
    private final CARD8 count;

    public static GetKeyboardMapping decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final KEYCODE firstKeycode = stream.readKEYCODE();
        final CARD8 count = stream.readCARD8();
        
        readUnusedCARD16(stream);

        return new GetKeyboardMapping(firstKeycode, count);
    }
    
    public GetKeyboardMapping(KEYCODE firstKeycode, CARD8 count) {

        Objects.requireNonNull(firstKeycode);
        Objects.requireNonNull(count);
        
        this.firstKeycode = firstKeycode;
        this.count = count;
    }

    public KEYCODE getFirstKeycode() {
        return firstKeycode;
    }

    public CARD8 getCount() {
        return count;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "firstKeycode", firstKeycode,
                "count", count
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeKEYCODE(firstKeycode);
        stream.writeCARD8(count);
        
        writeUnusedCARD16(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_KEYBOARD_MAPPING;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GetKeyboardMappingReply.class;
    }
}
