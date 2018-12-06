package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;

final class Atoms {

    private int allocator;
    
    private final Map<String, Atom> byName;
    
    Atoms() {
        this.allocator = 1;
        
        this.byName = new HashMap<>();
    }
    
    boolean contains(String name) {
        
        Objects.requireNonNull(name);
    
        return byName.containsKey(name);
    }
    
    ATOM getAtom(String name) {
        
        Objects.requireNonNull(name);
        
        final Atom atom = byName.get(name);
        
        return atom != null ? atom.getAtom() : null;
    }
    
    ATOM addIfNotExists(String name) {
        
        final Atom atom = byName.get(name);
        
        final ATOM result;
        
        if (atom == null) {
            result = new ATOM(allocator ++);
            
            byName.put(name, new Atom(name, result));
        }
        else {
            result = atom.getAtom();
        }
    
        return result;
    }
}
