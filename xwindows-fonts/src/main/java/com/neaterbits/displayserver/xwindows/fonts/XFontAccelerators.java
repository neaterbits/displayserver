package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Objects;

public final class XFontAccelerators {

    private final boolean noOverlap;
    private final boolean constantMetrics;
    private final boolean terminalFont;
    private final boolean constantWidth;
    private final boolean inkInside;
    private final boolean inkMetrics;
    private final int drawDirection;
    private final int fontAscent;
    private final int fontDescent;
    private final int maxOverlap;
    private final XFontCharacter minbounds;
    private final XFontCharacter maxbounds;
    private final XFontCharacter inkMinbounds;
    private final XFontCharacter inkMaxbounds;

    public XFontAccelerators(boolean noOverlap, boolean constantMetrics, boolean terminalFont, boolean constantWidth,
            boolean inkInside, boolean inkMetrics, int drawDirection, int fontAscent, int fontDescent, int maxOverlap,
            XFontCharacter minbounds, XFontCharacter maxbounds,
            XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds) {
        
        Objects.requireNonNull(minbounds);
        Objects.requireNonNull(maxbounds);
        
        this.noOverlap = noOverlap;
        this.constantMetrics = constantMetrics;
        this.terminalFont = terminalFont;
        this.constantWidth = constantWidth;
        this.inkInside = inkInside;
        this.inkMetrics = inkMetrics;
        this.drawDirection = drawDirection;
        this.fontAscent = fontAscent;
        this.fontDescent = fontDescent;
        this.maxOverlap = maxOverlap;
        this.minbounds = minbounds;
        this.maxbounds = maxbounds;
        this.inkMinbounds = inkMinbounds;
        this.inkMaxbounds = inkMaxbounds;
    }

    public boolean isNoOverlap() {
        return noOverlap;
    }
    
    public boolean isConstantMetrics() {
        return constantMetrics;
    }
    
    public boolean isTerminalFont() {
        return terminalFont;
    }
    
    public boolean isConstantWidth() {
        return constantWidth;
    }
    
    public boolean isInkInside() {
        return inkInside;
    }
    
    public boolean isInkMetrics() {
        return inkMetrics;
    }
    
    public int getDrawDirection() {
        return drawDirection;
    }
    
    public int getFontAscent() {
        return fontAscent;
    }
    
    public int getFontDescent() {
        return fontDescent;
    }
    
    public int getMaxOverlap() {
        return maxOverlap;
    }
    
    public XFontCharacter getMinbounds() {
        return minbounds;
    }
    
    public XFontCharacter getMaxbounds() {
        return maxbounds;
    }

    public XFontCharacter getInkMinbounds() {
        return inkMinbounds;
    }

    public XFontCharacter getInkMaxbounds() {
        return inkMaxbounds;
    }

    @Override
    public String toString() {
        return "XFontAccelerators [noOverlap=" + noOverlap + ", constantMetrics=" + constantMetrics + ", terminalFont="
                + terminalFont + ", constantWidth=" + constantWidth + ", inkInside=" + inkInside + ", inkMetrics="
                + inkMetrics + ", drawDirection=" + drawDirection + ", fontAscent=" + fontAscent + ", fontDescent="
                + fontDescent + ", maxOverlap=" + maxOverlap + ", minbounds=" + minbounds + ", maxbounds=" + maxbounds
                + ", inkMinbounds=" + inkMinbounds + ", inkMaxbounds=" + inkMaxbounds + "]";
    }
}
