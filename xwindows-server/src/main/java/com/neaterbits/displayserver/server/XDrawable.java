package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.VISUALID;

abstract class XDrawable {

    private final VISUALID visual;
    private final Map<GCONTEXT, XGC> gcs;

    XDrawable(VISUALID visual) {
        
        Objects.requireNonNull(visual);
        
        this.visual = visual;
        
        this.gcs = new HashMap<>();
    }
    
    final VISUALID getVisual() {
        return visual;
    }

    final void addGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);

        if (gcs.containsKey(context)) {
            throw new IllegalStateException();
        }
        
        final XGC xgc = new XGC(attributes);
        
        gcs.put(context, xgc);
    }

    final void removeGC(GCONTEXT context) {
        
        Objects.requireNonNull(context);
        
        gcs.remove(context);
    }
}
