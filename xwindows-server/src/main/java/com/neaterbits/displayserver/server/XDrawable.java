package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;

abstract class XDrawable {

    private final Map<GCONTEXT, XGC> gcs;

    XDrawable() {
        this.gcs = new HashMap<>();
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
