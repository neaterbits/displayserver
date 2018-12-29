package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;

public class PCFReaderTest {

    private final PCFReaderListener<Object> listener = new PCFReaderListener<Object>() {

        @Override
        public void onProperties(Object data, int count) {
            System.out.println("onProperties " + count);
        }

        @Override
        public void onIntegerProperty(Object data, String name, int value) {
            System.out.println("onIntegerProperty " + name + "/" + value);
        }

        @Override
        public void onStringProperty(Object data, String name, String value) {
            System.out.println("onStringProperty " + name + "/" + value);
        }

        @Override
        public void onAccelerators(Object data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
                boolean constantWidth, boolean inkInside, boolean inkMetrics, int drawDirection, int fontAscent,
                int fontDescent, int maxOverlap, XFontCharacter minbounds, XFontCharacter maxbounds,
                XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds) {
            
            System.out.println("onAccelerators [noOverlap=" + noOverlap + ", constantMetrics=" + constantMetrics + ", terminalFont="
                    + terminalFont + ", constantWidth=" + constantWidth + ", inkInside=" + inkInside + ", inkMetrics="
                    + inkMetrics + ", drawDirection=" + drawDirection + ", fontAscent=" + fontAscent + ", fontDescent="
                    + fontDescent + ", maxOverlap=" + maxOverlap + ", minbounds=" + minbounds + ", maxbounds=" + maxbounds
                    + ", inkMinbounds=" + inkMinbounds + ", inkMaxbounds=" + inkMaxbounds + "]"
            );
        }
        
        @Override
        public void onMetrics(Object data, int count) {
            System.out.println("onMetrics " + count);
        }

        @Override
        public void onMetric(Object data, XFontCharacter fontCharacter) {
            System.out.println("onMetric " + fontCharacter);
            
        }

        @Override
        public void onBitmaps(Object data, int count) {
            System.out.println("onBitmaps " + count);
        }

        @Override
        public void onBitmap(Object data, byte[] bitmapData) {
            System.out.println("onBitmap " + bitmapData.length);
        }

        @Override
        public void onInkMetrics(Object data, int count) {
            System.out.println("onInkMetrics " + count);
        }

        @Override
        public void onInkMetric(Object data, XFontCharacter fontCharacter) {
            System.out.println("onInkMetric " + fontCharacter);
        }

        @Override
        public void onEncodings(Object data, short minCharOrByte2, short maxCharOrByte2, short minByte1, short maxByte1,
                short defaultChar, short[] glyphIndices) {
            
            System.out.println("onEncodings [minCharOrByte2=" + minCharOrByte2 + ", maxCharOrByte2=" + maxCharOrByte2 + ", minByte1="
                    + minByte1 + ", maxByte1=" + maxByte1 + ", defaultChar=" + defaultChar + ", glyphIndices="
                    + glyphIndices.length + "]");
        }

        @Override
        public void onScalableWidths(Object data, int[] scalableWidths) {
            System.out.println("onScalableWidths " + scalableWidths.length);
        }

        @Override
        public void onGlyphNames(Object data, String[] names) {
            System.out.println("onGlyphNames " + names.length);
        }

        @Override
        public void onBdfAccelerators(Object data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
                boolean constantWidth, boolean inkInside, boolean inkMetrics, int drawDirection, int fontAscent,
                int fontDescent, int maxOverlap,
                XFontCharacter minbounds, XFontCharacter maxbounds,
                XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds) {

            System.out.println("onBdfAccelerators [noOverlap=" + noOverlap + ", constantMetrics=" + constantMetrics + ", terminalFont="
                    + terminalFont + ", constantWidth=" + constantWidth + ", inkInside=" + inkInside + ", inkMetrics="
                    + inkMetrics + ", drawDirection=" + drawDirection + ", fontAscent=" + fontAscent + ", fontDescent="
                    + fontDescent + ", maxOverlap=" + maxOverlap + ", minbounds=" + minbounds + ", maxbounds=" + maxbounds
                    + ", inkMinbounds=" + inkMinbounds + ", inkMaxbounds=" + inkMaxbounds + "]"
            );
        }
    };
    
    @Test
    public void testReadFile() throws FileNotFoundException, IOException {
        
        final String file = "/usr/share/fonts/X11/misc/olgl12.pcf.gz";
        
        try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {

            PCFReader.read(inputStream, listener, null);
            
        }
    }
}
