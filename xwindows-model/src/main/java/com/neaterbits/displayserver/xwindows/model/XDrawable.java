package com.neaterbits.displayserver.xwindows.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.util.Disposable;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public abstract class XDrawable implements Disposable {

    private final VISUALID visual;
    private final Map<GCONTEXT, XGC> gcs;

    private final XLibRenderer renderer;
    
    public abstract BufferOperations getBufferOperations();
    
    XDrawable(VISUALID visual, XLibRenderer renderer) {
        
        Objects.requireNonNull(visual);
        Objects.requireNonNull(renderer);
        
        this.visual = visual;
        this.renderer = renderer;
        
        this.gcs = new HashMap<>();
    }
    
    public final VISUALID getVisual() {
        return visual;
    }

    public final void addGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);

        if (gcs.containsKey(context)) {
            throw new IllegalStateException();
        }
        
        final XGC xgc = new XGC(attributes);
        
        gcs.put(context, xgc);
    }
    
    public final void changeGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);
        
        final XGC existing = gcs.get(context);
        
        if (existing == null) {
            throw new IllegalStateException();
        }

        gcs.put(context, new XGC(existing.getAttributes().applyImmutably(attributes)));
    }

    public final XGC getGC(GCONTEXT context) {
        
        Objects.requireNonNull(context);
        
        return gcs.get(context);
    }
    
    public final void removeGC(GCONTEXT context) {
        
        Objects.requireNonNull(context);
        
        gcs.remove(context);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}