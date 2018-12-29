package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.neaterbits.displayserver.protocol.enums.DrawDirection;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;

public class PCFReader {

    public static XFont read(String fontName, InputStream inputStream, Function<String, ATOM> getAtom) throws IOException {
    
        final XFontPCFReaderListener listener = new XFontPCFReaderListener(fontName, getAtom);
        
        read(inputStream, listener, null);
        
        return listener.getFont();
    }
    
    static <T> void read(InputStream inputStream, PCFReaderListener<T> listener, T data) throws IOException {
        
        final PCFStream dataInput = new PCFStream(inputStream);
        
        final TOC toc = readTOC(dataInput);

        int curOffset = 4 + 4 + (toc.getEntriesSorted().size() * 16);

        int entryIdx = 0;

        System.out.println("## cur offset " + curOffset);
        
        for (TOCEntry tocEntry : toc.getEntriesSorted()) {
            System.out.println("TocEntry of type " + tocEntry.getType() + " at " + tocEntry.getOffset() + " of size " + tocEntry.getSize());
        }

        for (TOCEntry tocEntry : toc.getEntriesSorted()) {
            
            System.out.println("## process of type " + tocEntry.getType() + ", stream offset = " + dataInput.getOffset() + ", curoffset=" + curOffset);
            
            if (tocEntry.getOffset() < curOffset) {
                throw new IOException("Offset mismatch: " + tocEntry.getOffset() + "/" + curOffset + " at idx " + entryIdx);
            }

            final int toSkip = tocEntry.getOffset() - curOffset;
            
            if (toSkip < 0) {
                throw new IllegalStateException();
            }
            
            curOffset += dataInput.skipBytes(toSkip);

            System.out.println("## process of type after skip " + tocEntry.getType() + ", stream offset = " + dataInput.getOffset() + ", curoffset=" + curOffset);

            switch (tocEntry.getType()) {
         
            case TOCEntry.PROPERTIES:
                curOffset = readProperties(dataInput, listener, data, curOffset);
                break;
                
            case TOCEntry.ACCELERATORS:
                curOffset = readAccelerators(dataInput, listener::onAccelerators, data, curOffset);
                break;
            
            case TOCEntry.METRICS:
                curOffset = readMetrics(dataInput, listener::onMetrics, listener::onMetric, data, curOffset);
                break;
                
            case TOCEntry.BITMAPS:
                curOffset = readBitmaps(dataInput, listener, data, curOffset);
                break;
                
            case TOCEntry.INK_METRICS:
                curOffset = readMetrics(dataInput, listener::onInkMetrics, listener::onInkMetric, data, curOffset);
                break;

            case TOCEntry.BDF_ENCODINGS:
                curOffset = readEncodings(dataInput, listener, data, curOffset);
                break;
                
            case TOCEntry.SWIDTHS:
                curOffset = readScalableWidths(dataInput, listener, data, curOffset);
                break;

            case TOCEntry.GLYPH_NAMES:
                curOffset = readGlyphNames(dataInput, listener, data, curOffset);
                break;

            case TOCEntry.BDF_ACCELERATORS:
                curOffset = readAccelerators(dataInput, listener::onBdfAccelerators, data, curOffset);
                break;

            default:
                System.out.println("## toc entry of type " + tocEntry.getType());
                break;
            }
            
            ++ entryIdx;
        }
    }
 
    private static TOC readTOC(PCFStream dataInput) throws IOException {
        
        final byte [] headerBytes = new byte[4];
        
        dataInput.readBytesOrEOF(headerBytes);
        
        final String header = new String(headerBytes);
        
        if (!header.equals("\1fcp")) {
            throw new IOException("Not a font file");
        }
        
        final int tableCount = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## tableCount : %08x\n", tableCount);
        
        final List<TOCEntry> entries = new ArrayList<>(tableCount);
        
        for (int i = 0; i < tableCount; ++ i) {
            
            final TOCEntry entry = new TOCEntry(
                    dataInput.readInt(ByteOrder.LITTLE_ENDIAN),
                    dataInput.readInt(ByteOrder.LITTLE_ENDIAN),
                    dataInput.readInt(ByteOrder.LITTLE_ENDIAN),
                    dataInput.readInt(ByteOrder.LITTLE_ENDIAN));
            
            entries.add(entry);
        }
        
        return new TOC(entries);
    }

    private static ByteOrder getByteOrder(int format) {

        final ByteOrder byteOrder = (format & PCF.BYTE_MASK) != 0
                ? ByteOrder.BIG_ENDIAN
                : ByteOrder.LITTLE_ENDIAN;

        return byteOrder;
    }
    
    private static <T> int readProperties(PCFStream dataInput, PCFReaderListener<T> listener, T data, int curOffset) throws IOException {

        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN); // format
        
        curOffset += 4;

        final ByteOrder byteOrder = getByteOrder(format);

        final int nprops = dataInput.readInt(byteOrder);

        curOffset += 4;

        final List<PropertyOffset> propertyOffsets = new ArrayList<>(nprops);

        curOffset = readPropertyOffsets(dataInput, byteOrder, curOffset, nprops, propertyOffsets);
        
        final List<Integer> offsets = new ArrayList<>(propertyOffsets.size() * 2);
        
        for (PropertyOffset propertyOffset : propertyOffsets) {
            offsets.add(propertyOffset.nameOffset);
            
            if (propertyOffset.isStringProp) {
                offsets.add(propertyOffset.value);
            }
        }
        
        Collections.sort(offsets);

        final Map<Integer, String> map = new HashMap<>(offsets.size());
        
        curOffset = readStrings(dataInput, byteOrder, offsets, curOffset, map);
        
        listener.onProperties(data, propertyOffsets.size());
        
        for (PropertyOffset propertyOffset : propertyOffsets) {
            
            final String name = map.get(propertyOffset.nameOffset);
            
            if (propertyOffset.isStringProp) {
                final String value = map.get(propertyOffset.value);
            
                listener.onStringProperty(data, name, value);
            }
            else {
                listener.onIntegerProperty(data, name, propertyOffset.value);
            }
        }
        
        return curOffset;
    }
    
    private static int pad32(int length) {
        return (4 - (length % 4)) % 4;
    }
    
    private static int readPropertyOffsets(PCFStream dataInput, ByteOrder byteOrder, int startOffset, int nprops, List<PropertyOffset> list) throws IOException {
        
        int curOffset = startOffset;
        
        for (int i = 0; i < nprops; ++ i) {
            final PropertyOffset propertyOffset = new PropertyOffset(
                    dataInput.readInt(byteOrder),
                    dataInput.readByte() != 0,
                    dataInput.readInt(byteOrder));
        
            list.add(propertyOffset);
            
            curOffset += 9;
        }
        
        // final int padding = (nprops & 3) == 0 ? 0 :( 4 - (nprops & 3));
        
        final int padding = pad32(curOffset);
        
        curOffset += padding;
        
        if (curOffset % 4 != 0) {
            throw new IllegalStateException();
        }
        
        dataInput.skipBytes(padding);
        
        return curOffset;
    }
    
    private static int readStrings(
            PCFStream dataInput,
            ByteOrder byteOrder,
            List<Integer> offsets,
            int startOffset,
            Map<Integer, String> map) throws IOException {

        int curOffset = startOffset;
        
        final int stringSize = dataInput.readInt(byteOrder);
        
        curOffset += 4;
        
        
        final int stringStartOffset = curOffset;
        
        curOffset = readStrings(
                dataInput,
                byteOrder,
                offsets,
                stringStartOffset,
                stringSize,
                (index, offset, string) -> map.put(offset, string));
        
        final int padding = pad32(curOffset);
        
        dataInput.skipBytes(padding);

        curOffset += padding;
        
        if (curOffset % 4 != 0) {
            throw new IllegalStateException();
        }
        
        return curOffset;
    }

    @FunctionalInterface
    interface ProcessString {
        void onString(int index, int offset, String string);
    }
    
    private static int readStrings(
            PCFStream dataInput,
            ByteOrder byteOrder,
            List<Integer> offsets,
            int stringStartOffset,
            int stringSize,
            ProcessString map) throws IOException {
        
        int curOffset = stringStartOffset;

        final StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < offsets.size(); ++ i) {
            
            final int offset = offsets.get(i);
            
            if (offset + stringStartOffset != curOffset) {
                throw new IOException("Offset mismatch: " + (offset + stringStartOffset) + "/" + curOffset);
            }
            
            sb.setLength(0);
            
            curOffset += dataInput.readString(sb);
            
            map.onString(i, offset, sb.toString());
        }

        if (curOffset != stringStartOffset + stringSize) {
            throw new IOException("Stringsize mismatch");
        }
        
        return curOffset;
    }
    
    
    private static class PropertyOffset {
        
        private final int nameOffset;
        private final boolean isStringProp;
        private final int value;

        PropertyOffset(int nameOffset, boolean isStringProp, int value) {
            this.nameOffset = nameOffset;
            this.isStringProp = isStringProp;
            this.value = value;
        }
    }

    @FunctionalInterface
    interface OnAccelerators<T> {
        void onAccelerators(T data, boolean noOverlap, boolean constantMetrics, boolean terminalFont,
                boolean constantWidth, boolean inkInside, boolean inkMetrics,
                int drawDirection, int fontAscent, int fontDescent, int maxOverlap,
                XFontCharacter minbounds, XFontCharacter maxbounds,
                XFontCharacter inkMinbounds, XFontCharacter inkMaxbounds);

    }
    
    private static <T> int readAccelerators(PCFStream dataInput, OnAccelerators<T> listener, T data, int startOffset) throws IOException {

        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN); // format
        
        curOffset += 4;

        System.out.format("## format: %08x\n", format);
        
        final ByteOrder byteOrder = getByteOrder(format);
        
        final short noOverlap = dataInput.readUnsignedByte();
        final short constantMetrics = dataInput.readUnsignedByte();
        
        final short terminalFont = dataInput.readUnsignedByte();
        final short constantWidth = dataInput.readUnsignedByte();
        final short inkInside = dataInput.readUnsignedByte();
        final short inkMetrics = dataInput.readUnsignedByte();
        final short drawDirection = dataInput.readUnsignedByte();
        
        dataInput.readUnsignedByte(); // padding
        
        curOffset += 8;
        
        final int fontAscent = dataInput.readInt(byteOrder);
        final int fontDescent = dataInput.readInt(byteOrder);
        
        final int maxOverlap = dataInput.readInt(byteOrder);

        curOffset += 12;
        
        final XFontCharacter minbounds = readUncompressedMetrics(dataInput, byteOrder);
        
        curOffset += 12;
        
        final XFontCharacter maxbounds = readUncompressedMetrics(dataInput, byteOrder);

        curOffset += 12;

        final XFontCharacter inkMinbounds;
        final XFontCharacter inkMaxbounds;
        
        if ((format & PCF.ACCEL_W_INKBOUNDS) != 0) {
            inkMinbounds = readUncompressedMetrics(dataInput, byteOrder);
            
            curOffset += 12;
            
            inkMaxbounds = readUncompressedMetrics(dataInput, byteOrder);

            curOffset += 12;
        }
        else {
            inkMinbounds = null;
            inkMaxbounds = null;
        }
        
        listener.onAccelerators(
                data,
                bool(noOverlap),
                bool(constantMetrics),
                bool(terminalFont),
                bool(constantWidth),
                bool(inkInside),
                bool(inkMetrics),
                drawDirection == 0 ? DrawDirection.LEFT_TO_RIGHT : DrawDirection.RIGHT_TO_LEFT,
                fontAscent,
                fontDescent,
                maxOverlap,
                minbounds,
                maxbounds,
                inkMinbounds,
                inkMaxbounds);
        
        return curOffset;
    }

    private static boolean bool(short value) {

        final boolean result;
        
        switch (value) {
        case 0:
            result = false;
            break;
            
        case 1:
            result = true;
            break;
            
        default:
            throw new IllegalArgumentException("Unknown value " + value);
        }
        
        return result;
    }

    @FunctionalInterface
    interface OnMetrics<T> {
        void onMetrics(T data, int count);
    }
    
    @FunctionalInterface
    interface OnMetric<T> {
        void onMetric(T data, XFontCharacter fontCharacter);        
    }
    
    private static <T> int readMetrics(
            PCFStream dataInput,
            OnMetrics<T> onMetricsCount,
            OnMetric<T> onMetricCharacter,
            T data,
            int startOffset) throws IOException {
        
        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## metrics format: %08x\n", format);
        
        curOffset += 4;
        
        final ByteOrder byteOrder = getByteOrder(format);

        if ((format & PCF.COMPRESSED_METRICS) != 0) {
            final int metricsCount = dataInput.readUnsignedShort(byteOrder);

            onMetricsCount.onMetrics(data, metricsCount);

            curOffset += 2;
            
            for (int i = 0; i < metricsCount; ++ i) {
                final XFontCharacter fontCharacter = readCompressedMetrics(dataInput, byteOrder);
            
                onMetricCharacter.onMetric(data, fontCharacter);
                
                curOffset += 5;
            }
        }
        else {
            final int metricsCount = dataInput.readInt(byteOrder);
            
            System.out.format("## metrics count: %08x\n", metricsCount);
            
            onMetricsCount.onMetrics(data, metricsCount);
            
            curOffset += 4;
            
            for (int i = 0; i < metricsCount; ++ i) {
                final XFontCharacter fontCharacter = readUncompressedMetrics(dataInput, byteOrder);
            
                onMetricCharacter.onMetric(data, fontCharacter);
                
                curOffset += 12;
            }
        }
        
        return curOffset;
    }

    private static <T> int readBitmaps(PCFStream dataInput, PCFReaderListener<T> listener, T data, int startOffset) throws IOException {
        
        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## bitmaps format: %08x\n", format);
        
        curOffset += 4;
        
        final ByteOrder byteOrder = getByteOrder(format);
    
        final int glyphCount = dataInput.readInt(byteOrder);
        
        curOffset += 4;

        final int [] offsets = new int[glyphCount];
        
        for (int i = 0; i < glyphCount; ++ i) {

            offsets[i] = dataInput.readInt(byteOrder);
            
            curOffset += 4;
        }

        final int [] bitmapSizes = new int[4];
        
        for (int i = 0; i < bitmapSizes.length; ++ i) {
            bitmapSizes[i] = dataInput.readInt(byteOrder);

            curOffset += 4;
        }

        final int bitmapSize = bitmapSizes[format & 3];
        
        System.out.println("## bitmap size " + bitmapSize);
        
        listener.onBitmaps(data, glyphCount);
        
        for (int i = 0; i < offsets.length; ++ i) {
            
            final int size;
            
            if (i < offsets.length - 1) {
                size = offsets[i + 1] - offsets[i];
            }
            else {
                size = bitmapSize - offsets[i];
            }
            
            final byte [] bytes = new byte[size];
            
            final int bytesRead = dataInput.readBytesOrEOF(bytes);
            
            if (bytesRead != size) {
                throw new IllegalStateException("bytesRead != size " + bytesRead + "/" + size);
            }
            
            listener.onBitmap(data, bytes);
            
            curOffset += bytesRead;
        }
        
        return curOffset;
    }

    private static <T> int readEncodings(PCFStream dataInput, PCFReaderListener<T> listener, T data, int startOffset) throws IOException {
        
        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## encodings format: %08x\n", format);
        
        curOffset += 4;
        
        final ByteOrder byteOrder = getByteOrder(format);
        
        final short minCharOrByte2 = dataInput.readShort(byteOrder);
        final short maxCharOrByte2 = dataInput.readShort(byteOrder);
        
        curOffset += 4;
        
        final short minByte1 = dataInput.readShort(byteOrder);
        final short maxByte1 = dataInput.readShort(byteOrder);

        curOffset += 4;

        final short defaultChar = dataInput.readShort(byteOrder);

        curOffset += 2;
        
        final int glyphIndicesCount = (maxCharOrByte2 - minCharOrByte2 + 1) * (maxByte1 - minByte1 + 1);
        
        final short [] glyphIndices = new short[glyphIndicesCount];
        
        for (int i = 0; i < glyphIndicesCount; ++ i) {
            
            glyphIndices[i] = dataInput.readShort(byteOrder);
            
            curOffset += 2;
        }
        
        listener.onEncodings(data, minCharOrByte2, maxCharOrByte2, minByte1, maxByte1, defaultChar, glyphIndices);
        
        return curOffset;
    }

    private static <T> int readScalableWidths(PCFStream dataInput, PCFReaderListener<T> listener, T data, int startOffset) throws IOException {
        
        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## scalable widths format: %08x\n", format);
        
        curOffset += 4;
        
        final ByteOrder byteOrder = getByteOrder(format);
     
        final int glyphCount = dataInput.readInt(byteOrder);
        
        curOffset += 4;
        
        final int [] scalableWidths = new int[glyphCount];
        
        for (int i = 0; i < glyphCount; ++ i) {
            scalableWidths[i] = dataInput.readInt(byteOrder);

            curOffset += 4;
        }
        
        listener.onScalableWidths(data, scalableWidths);
        
        return curOffset;
    }

    private static <T> int readGlyphNames(PCFStream dataInput, PCFReaderListener<T> listener, T data, int startOffset) throws IOException {
        
        int curOffset = startOffset;
        
        final int format = dataInput.readInt(ByteOrder.LITTLE_ENDIAN);
        
        System.out.format("## scalable widths format: %08x\n", format);
        
        curOffset += 4;
        
        final ByteOrder byteOrder = getByteOrder(format);
     
        final int glyphCount = dataInput.readInt(byteOrder);
        
        curOffset += 4;
        
        final List<Integer> offsets = new ArrayList<>(glyphCount);
        
        for (int i = 0; i < glyphCount; ++ i) {
            offsets.add(dataInput.readInt(byteOrder));

            curOffset += 4;
        }

        final int stringSize = dataInput.readInt(byteOrder);
        
        curOffset += 4;
        
        final String [] names = new String[glyphCount];
        
        curOffset = readStrings(
                dataInput,
                byteOrder,
                offsets,
                curOffset,
                stringSize,
                (index, offset, name) -> names[index] = name);

        listener.onGlyphNames(data, names);
        
        return curOffset;
    }

    private static <T> XFontCharacter readUncompressedMetrics(PCFStream dataInput, ByteOrder byteOrder) throws IOException {
        
        final short leftSideBearing     = dataInput.readShort(byteOrder);
        final short rigthSideBearing    = dataInput.readShort(byteOrder);
        final short characterWidth      = dataInput.readShort(byteOrder);
        final short ascent              = dataInput.readShort(byteOrder);
        final short descent             = dataInput.readShort(byteOrder);
        final int attributes            = dataInput.readUnsignedShort(byteOrder);
        
        return new XFontCharacter(
                leftSideBearing, rigthSideBearing,
                characterWidth,
                ascent, descent,
                attributes);
    }

    private static short readCompressed(PCFStream dataInput) throws IOException {
        return (short)(dataInput.readByte() - 0x80);
    }
    
    private static <T> XFontCharacter readCompressedMetrics(PCFStream dataInput, ByteOrder byteOrder) throws IOException {
        
        final short leftSideBearing     = readCompressed(dataInput);
        final short rigthSideBearing    = readCompressed(dataInput);
        final short characterWidth      = readCompressed(dataInput);
        final short ascent              = readCompressed(dataInput);
        final short descent             = readCompressed(dataInput);
        final int attributes            = 0;
        
        return new XFontCharacter(
                leftSideBearing, rigthSideBearing,
                characterWidth,
                ascent, descent,
                attributes);
    }
}
