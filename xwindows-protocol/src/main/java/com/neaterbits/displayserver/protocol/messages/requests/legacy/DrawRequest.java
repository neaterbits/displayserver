package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

public abstract class DrawRequest extends Request {

    private final DRAWABLE drawable;
    private final GCONTEXT gc;

    public DrawRequest(DRAWABLE drawable, GCONTEXT gc) {

        Objects.requireNonNull(drawable);
        Objects.requireNonNull(gc);
        
        this.drawable = drawable;
        this.gc = gc;
    }

    public final DRAWABLE getDrawable() {
        return drawable;
    }

    public final GCONTEXT getGC() {
        return gc;
    }
}
