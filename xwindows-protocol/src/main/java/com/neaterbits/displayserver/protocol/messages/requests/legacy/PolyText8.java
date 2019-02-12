package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Collection;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.TEXTITEM8;

public final class PolyText8 extends PolyTextRequest<String, TEXTITEM8>{

    public static PolyText8 decode(XWindowsProtocolInputStream stream) throws IOException {
        
        return decode(stream, TEXTITEM8::decode, PolyText8::new);
    }
    
    private PolyText8(DRAWABLE drawable, GCONTEXT gc, INT16 x, INT16 y, Collection<TEXTITEM8> items) {
        this(drawable, gc, x, y, items.toArray(new TEXTITEM8[items.size()]));
    }

    
    public PolyText8(DRAWABLE drawable, GCONTEXT gc, INT16 x, INT16 y, TEXTITEM8[] items) {
        super(drawable, gc, x, y, items);
    }

    @Override
    public int getOpCode() {
        return OpCodes.POLY_TEXT_8;
    }
}
