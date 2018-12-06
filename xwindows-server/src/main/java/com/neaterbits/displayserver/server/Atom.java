package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;

final class Atom {

    private final String name;
    private final ATOM atom;
    
    private byte [] value;
    
    String getName() {
        return name;
    }

    ATOM getAtom() {
        return atom;
    }

    byte[] getValue() {
        return value;
    }

    void setValue(byte[] value) {
        this.value = value;
    }

    Atom(String name, ATOM atom) {
        this(name, atom, null);
    }

    Atom(String name, ATOM atom, byte[] value) {
        
        Objects.requireNonNull(name);
        Objects.requireNonNull(atom);
        
        this.name = name;
        this.atom = atom;
        this.value = value;
    }
}
