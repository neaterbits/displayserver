package com.neaterbits.displayserver.protocol.types;

import java.util.Arrays;
import java.util.Objects;

public final class STRING16 {

    private final CHAR2B [] characters;

    public STRING16(CHAR2B[] characters) {
        
        Objects.requireNonNull(characters);
        
        this.characters = characters;
    }

    public CHAR2B getCharacter(int index) {
        return characters[index];
    }
    
    public int length() {
        return characters.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(characters);
    }
}
