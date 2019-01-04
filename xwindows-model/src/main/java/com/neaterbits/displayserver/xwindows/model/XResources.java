package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;

public abstract class XResources<T extends XResource> {

    public abstract Collection<T> getResources();
    
}
