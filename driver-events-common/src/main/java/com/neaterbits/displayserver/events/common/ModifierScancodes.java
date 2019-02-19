package com.neaterbits.displayserver.events.common;

import java.util.Collections;
import java.util.List;

public final class ModifierScancodes {

    private final int codesPerModifier;
    private final List<ModifierMapping> modifiers;
    
    public ModifierScancodes(int codesPerModifier, List<ModifierMapping> modifiers) {
        this.codesPerModifier = codesPerModifier;
        this.modifiers = Collections.unmodifiableList(modifiers);
        
        for (ModifierMapping modifier : modifiers) {
            if (modifier.getScancodes().length != codesPerModifier) {
                throw new IllegalArgumentException();
            }
        }
    }

    public int getCodesPerModifier() {
        return codesPerModifier;
    }

    public List<ModifierMapping> getModifiers() {
        return modifiers;
    }
}
