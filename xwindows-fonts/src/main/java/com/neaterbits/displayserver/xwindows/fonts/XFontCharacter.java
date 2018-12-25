package com.neaterbits.displayserver.xwindows.fonts;

public final class XFontCharacter {
    private final short leftSideBearing;
    private final short rigthSideBearing;
    private final short characterWidth;
    private final short ascent;
    private final short descent;

    private final int attributes;

    public XFontCharacter(short leftSideBearing, short rigthSideBearing, short characterWidth, short ascent, short descent,
            int attributes) {

        this.leftSideBearing = leftSideBearing;
        this.rigthSideBearing = rigthSideBearing;
        this.characterWidth = characterWidth;
        this.ascent = ascent;
        this.descent = descent;
        this.attributes = attributes;
    }

    public short getLeftSideBearing() {
        return leftSideBearing;
    }

    public short getRigthSideBearing() {
        return rigthSideBearing;
    }

    public short getCharacterWidth() {
        return characterWidth;
    }

    public short getAscent() {
        return ascent;
    }

    public short getDescent() {
        return descent;
    }

    public int getAttributes() {
        return attributes;
    }

    public boolean isNonZero() {
        return characterWidth != 0;
    }
    
    @Override
    public String toString() {
        return "XFontCharacter [leftSideBearing=" + leftSideBearing + ", rigthSideBearing=" + rigthSideBearing
                + ", characterWidth=" + characterWidth + ", ascent=" + ascent + ", descent=" + descent + ", attributes="
                + attributes + "]";
    }
}
