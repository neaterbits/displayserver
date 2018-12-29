package com.neaterbits.displayserver.xwindows.util;

public class Refcountable {

    private int refCount;
    
    protected void onNoRefs() {
        
    }
    
    public Refcountable() {
        this.refCount = 1;
    }
    
    public final void addRef() {
    
        ++ refCount;
        
    }
    
    public final boolean remRef() {
        
        if (refCount == 0) {
            throw new IllegalArgumentException();
        }
        
        if (refCount == 0) {
            onNoRefs();
        }
        else {
            -- refCount;
        }
        
        return refCount != 0;
    }
}
