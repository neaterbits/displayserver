package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class XFont {

    private final List<XFontProperty> properties;
    private final XFontAccelerators accelerators;
    private final List<XFontCharacter> metrics;
    private final XFontBitmaps bitmaps;
    private final List<XFontCharacter> inkMetrics;
    private final XFontEncodings encodings;
    private final int [] scalableWidths;
    private final String [] glyphNames;

    public XFont(
            List<XFontProperty> properties,
            XFontAccelerators accelerators,
            List<XFontCharacter> metrics,
            XFontBitmaps bitmaps,
            List<XFontCharacter> inkMetrics,
            XFontEncodings encodings,
            int [] scalableWidths,
            String [] names) {

        Objects.requireNonNull(properties);
        Objects.requireNonNull(accelerators);
        Objects.requireNonNull(metrics);
        Objects.requireNonNull(bitmaps);
//        Objects.requireNonNull(inkMetrics);
        Objects.requireNonNull(encodings);
        Objects.requireNonNull(scalableWidths);
        Objects.requireNonNull(names);
        
        this.properties = Collections.unmodifiableList(properties);
        this.accelerators = accelerators;
        this.metrics = Collections.unmodifiableList(metrics);
        this.bitmaps = bitmaps;
        this.inkMetrics = inkMetrics;
        this.encodings = encodings;
        this.scalableWidths = scalableWidths;
        this.glyphNames = names;
    }

    public List<XFontProperty> getProperties() {
        return properties;
    }

    public XFontAccelerators getAccelerators() {
        return accelerators;
    }

    public List<XFontCharacter> getMetrics() {
        return metrics;
    }

    public XFontBitmaps getBitmaps() {
        return bitmaps;
    }

    public List<XFontCharacter> getInkMetrics() {
        return inkMetrics;
    }

    public XFontEncodings getEncodings() {
        return encodings;
    }

    public int[] getScalableWidths() {
        return scalableWidths;
    }

    public String[] getGlyphNames() {
        return glyphNames;
    }
}
