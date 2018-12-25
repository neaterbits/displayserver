package com.neaterbits.displayserver.events.common;

import java.util.Collections;
import java.util.List;

public final class ModifierScancodes {

    private final int codesPerModifier;
    private final List<Modifier> modifiers;
    
    public ModifierScancodes(int codesPerModifier, List<Modifier> modifiers) {
        this.codesPerModifier = codesPerModifier;
        this.modifiers = Collections.unmodifiableList(modifiers);
        
        for (Modifier modifier : modifiers) {
            if (modifier.getScancodes().length != codesPerModifier) {
                throw new IllegalArgumentException();
            }
        }
    }

    public int getCodesPerModifier() {
        return codesPerModifier;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }
}
