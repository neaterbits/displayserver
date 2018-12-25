package com.neaterbits.displayserver.xwindows.fonts.pcf;

import com.neaterbits.displayserver.xwindows.fonts.XFontCharacter;

public interface PCFReaderListener<T> {

    void onProperties(T data, int count);
    
    void onIntegerProperty(T data, String name, int value);
    
    void onStringProperty(T data, String name, String value);

    void onAccelerators(T data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
            boolean constantWidth, boolean inkInside, boolean inkMetrics,
            int drawDirection, int fontAscent, int fontDescent, int maxOverlap,
            XFontCharacter minbounds, XFontCharacter maxbounds,
            XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds);

    void onMetrics(T data, int count);
    
    void onMetric(T data, XFontCharacter fontCharacter);

    void onBitmaps(T data, int count);
    
    void onBitmap(T data, byte [] bitmapData);

    void onInkMetrics(T data, int count);
    
    void onInkMetric(T data, XFontCharacter fontCharacter);
    
    void onEncodings(T data,
            short minCharOrByte2, short maxCharOrByte2,
            short minByte1, short maxByte1,
            short defaultChar,
            short [] glyphIndices);

    void onScalableWidths(T data, int [] scalableWidths);
    
    void onGlyphNames(T data, String [] names);

    void onBdfAccelerators(T data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
            boolean constantWidth, boolean inkInside, boolean inkMetrics,
            int drawDirection, int fontAscent, int fontDescent, int maxOverlap,
            XFontCharacter minbounds, XFontCharacter maxbounds,
            XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds);
}
