package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD32;

public final class FONTPROP extends Encodeable {

    private final ATOM name;
    private final CARD32 value;

    public FONTPROP(ATOM name, CARD32 value) {
        
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        
        this.name = name;
        this.value = value;
    }

    public ATOM getName() {
        return name;
    }

    public CARD32 getValue() {
        return value;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "name", name,
                "value", value
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeATOM(name);
        stream.writeCARD32(value);
    }
}
