package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.xwindows.fonts.model.FontBitmapFormat;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontAccelerators;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontBitmaps;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontEncodings;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontIntegerProperty;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontStringProperty;

final class XFontPCFReaderListener implements PCFReaderListener<Void> {

    private List<XFontProperty> properties;
    private XFontAccelerators accelerators;
    private List<XFontCharacter> metrics;
    
    private FontBitmapFormat bitmapFormat;
    private List<byte[]> bitmaps;

    private List<XFontCharacter> inkMetrics;
    
    private XFontEncodings encodings;
    
    private int [] scalableWidths;
    private String [] glyphNames;

    private XFontAccelerators bdfAccelerators;
    
    @Override
    public void onProperties(Void data, int count) {

        if (properties != null) {
            throw new IllegalStateException();
        }
        
        this.properties = new ArrayList<>(count);
    }

    @Override
    public void onIntegerProperty(Void data, String name, int value) {
        properties.add(new XFontIntegerProperty(name, value));
    }

    @Override
    public void onStringProperty(Void data, String name, String value) {
        properties.add(new XFontStringProperty(name, value));
    }

    @Override
    public void onAccelerators(Void data, boolean noOverlap, boolean constantMetrics, boolean terminalFont, boolean constantWidth,
            boolean inkInside, boolean inkMetrics, int drawDirection, int fontAscent, int fontDescent, int maxOverlap,
            XFontCharacter minbounds, XFontCharacter maxbounds,
            XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds) {
        
        if (this.accelerators != null) {
            throw new IllegalStateException();
        }

        this.accelerators = new XFontAccelerators(
                noOverlap, constantMetrics, terminalFont, constantWidth,
                inkInside, inkMetrics,
                drawDirection,
                fontAscent, fontDescent,
                maxOverlap,
                minbounds, maxbounds,
                inkMinbounds, inkMaxbounds);
    }
    
    @Override
    public void onMetrics(Void data, int count) {
        if (this.metrics != null) {
            throw new IllegalStateException();
        }
        
        this.metrics = new ArrayList<>(count);
    }

    @Override
    public void onMetric(Void data, XFontCharacter fontCharacter) {

        Objects.requireNonNull(fontCharacter);
        
        metrics.add(fontCharacter);
    }
    
    @Override
    public void onBitmaps(Void data, FontBitmapFormat bitmapFormat, int count) {
        
        Objects.requireNonNull(bitmapFormat);
        
        if (this.bitmapFormat != null) {
            throw new IllegalStateException();
        }
        
        if (this.bitmaps != null) {
            throw new IllegalStateException();
        }
        
        this.bitmapFormat = bitmapFormat;
        this.bitmaps = new ArrayList<>(count);
    }

    @Override
    public void onBitmap(Void data, byte[] bitmapData) {
        Objects.requireNonNull(bitmapData);

        bitmaps.add(bitmapData);
    }

    @Override
    public void onInkMetrics(Void data, int count) {
        if (this.inkMetrics != null) {
            throw new IllegalStateException();
        }
        
        this.inkMetrics = new ArrayList<>(count);
    }

    @Override
    public void onInkMetric(Void data, XFontCharacter fontCharacter) {

        Objects.requireNonNull(fontCharacter);
        
        inkMetrics.add(fontCharacter);
    }

    @Override
    public void onEncodings(Void data, short minCharOrByte2, short maxCharOrByte2, short minByte1, short maxByte1,
            short defaultChar, short[] glyphIndices) {

        if (this.encodings != null) {
            throw new IllegalStateException();
        }
        
        this.encodings = new XFontEncodings(minCharOrByte2, maxCharOrByte2, minByte1, maxByte1, defaultChar, glyphIndices);
    }

    @Override
    public void onScalableWidths(Void data, int[] scalableWidths) {

        Objects.requireNonNull(scalableWidths);
        
        if (this.scalableWidths != null) {
            throw new IllegalStateException();
        }
        
        this.scalableWidths = scalableWidths;
    }
    
    @Override
    public void onGlyphNames(Void data, String[] names) {
        
        Objects.requireNonNull(names);
        
        if (this.glyphNames != null) {
            throw new IllegalStateException();
        }
    
        this.glyphNames = names;
    }

    
    @Override
    public void onBdfAccelerators(Void data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
            boolean constantWidth, boolean inkInside, boolean inkMetrics, int drawDirection, int fontAscent,
            int fontDescent, int maxOverlap,
            XFontCharacter minbounds, XFontCharacter maxbounds,
            XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds) {

        if (this.bdfAccelerators != null) {
            throw new IllegalStateException();
        }

        this.bdfAccelerators = new XFontAccelerators(
                noOverlap, constantMetrics, terminalFont, constantWidth,
                inkInside, inkMetrics,
                drawDirection,
                fontAscent, fontDescent,
                maxOverlap,
                minbounds, maxbounds,
                inkMinbounds, inkMaxbounds);
    }

    List<XFontProperty> getProperties() {
        return properties;
    }
    
    XFontModel getFontModel() {
        return new XFontModel(
                properties,
                accelerators,
                metrics,
                new XFontBitmaps(bitmapFormat, bitmaps),
                inkMetrics,
                encodings,
                scalableWidths,
                glyphNames);
    }
}
