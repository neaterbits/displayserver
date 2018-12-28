package com.neaterbits.displayserver.util;

public abstract class NativeReference implements Disposable {

    private final long reference;

    protected NativeReference(long reference) {
        this.reference = reference;
    }

    protected final long getReference() {
        return reference;
    }
}
