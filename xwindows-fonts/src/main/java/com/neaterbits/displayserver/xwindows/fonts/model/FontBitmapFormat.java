package com.neaterbits.displayserver.xwindows.fonts.model;

import java.io.PrintStream;
import java.util.Objects;

import com.neaterbits.displayserver.xwindows.util.Padding;

public final class FontBitmapFormat {

    private final StoreOrder byteOrder;
    private final StoreOrder bitOrder;
    private final DataLength padding;
    private final DataLength storage;
    
    public static boolean DEBUG = false;
    
    public FontBitmapFormat(StoreOrder byteOrder, StoreOrder bitOrder, DataLength padding, DataLength storage) {

        Objects.requireNonNull(byteOrder);
        Objects.requireNonNull(bitOrder);
        Objects.requireNonNull(padding);
        Objects.requireNonNull(storage);
        
        this.byteOrder = byteOrder;
        this.bitOrder = bitOrder;
        this.padding = padding;
        this.storage = storage;
    }
    
    
    public void printFontBitmap(
            PrintStream out,
            byte [] from,
            int width, int height) {
        
            
        for (int y = 0; y < height; ++ y) {

            for (int x = 0; x < width; ++ x) {
               
                if (isSet(from, x, y, width)) {
                    out.print("#");
                }
                else {
                    out.print(" ");
                }
            }

            out.println();
        }
    }
    
    public static void convert(
            FontBitmapFormat fromFormat, byte [] from,
            FontBitmapFormat toFormat, byte [] to,
            int width, int height) {
        
        for (int x = 0; x < width; ++ x) {
            
            for (int y = 0; y < height; ++ y) {
               
                if (fromFormat.isSet(from, x, y, width)) {
                    toFormat.set(to, x, y, width);
                }
                else {
                }
            }
        }
    }

    private int getBytes(int width) {
        return (width - 1) / 8 + 1;
    }
    
    int getStride(int width) {
        
        final int bytes = getBytes(width);
        final int stride = Padding.getPaddedLength(bytes, padding.getBytes());

        return stride;
    }
    
    private long getIndex(int x, int y, int width) {

        final int stride = getStride(width);
        
        final int lineIndex = y * stride;

        final int lineByteIndex;
        final long bitIndex;
        
        switch (storage) {
        case BYTE: {
            lineByteIndex = x / 8;
            
            final int mod = x % 8;
            
            switch (bitOrder) {
            case LEAST_SIGNIFICANT_FIRST:
                bitIndex = 8 - mod;
                break;
                
            case MOST_SIGNIFICANT_FIRST:
                bitIndex = mod;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
        }
            
        /*
        case SHORT: {
            
            final int shortIndex = x / 16;
            final int mod = x % 16;
            
            switch (bitOrder) {
            case LEAST_SIGNIFICANT_FIRST:
                if (mod >= 8) {
                    lineByteIndex = (shortIndex * 2) + 1;
                    bitIndex = 16 - mod;
                }
                else {
                    lineByteIndex = shortIndex * 2;
                    bitIndex = mod;
                }
                break;
                
            case MOST_SIGNIFICANT_FIRST:
                if (mod >= 8) {
                    lineByteIndex = shortIndex * 2;
                    bitIndex = 16 - (16 - mod);
                }
                else {
                    lineByteIndex = shortIndex * 2;
                    bitIndex = 16 - mod;
                }
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
        }
        */
            
        case INT: {
            final int intIndex = x / 32; // index into int array
            
            final int intByteIndex = intIndex * 4; // index into byte array
            
            final int intBitIndex = x % 32;
            
            final int bitsByteIndex = intBitIndex / 8; // byte-index into 32 bit int
            
            final int bitsMod = x % 8;
            
            switch (bitOrder) {
            case LEAST_SIGNIFICANT_FIRST:
                if (DEBUG) {
                    System.out.println("## intByteIndex: " + intByteIndex + ", bitsByteIndex: " + bitsByteIndex + ", intBitIndex: " + intBitIndex);
                }
                lineByteIndex = intByteIndex + bitsByteIndex;
                bitIndex = bitsMod;
                break;
                
            case MOST_SIGNIFICANT_FIRST:
                lineByteIndex = intByteIndex + (4 - bitsByteIndex);
                bitIndex = 8 - bitsMod;
                break;

            default:
                throw new UnsupportedOperationException();
            }
            break;
        }
            
        default:
            throw new UnsupportedOperationException();
        }
        
        if (DEBUG) {
            System.out.println("## lineIndex: " + lineIndex + ", lineByteIndex: " + lineByteIndex +", bytes: " + getBytes(width) + ", stride: " + stride);
        }
        
        final long byteIndex = lineIndex + lineByteIndex;
        
        return (byteIndex << 32) | bitIndex;
    }
    
    public boolean isSet(byte [] byteArray, int x, int y, int width) {
        
        final long index = getIndex(x, y, width);
        
        final int byteIndex = (int)(index >> 32);
        final int bitIndex = (int)(index & 0xFF);
        
        final boolean isSet = (byteArray[byteIndex] & (1 << bitIndex)) != 0;
        
        if (DEBUG) {
            System.out.println("## testing " + byteIndex + "/" + bitIndex
                    + " from " + x + "/" + y + ", bytes: " + byteArray.length + ", isSet=" + isSet);
        }
        
        return isSet;
    }
    
    public void set(byte [] byteArray, int x, int y, int width) {
        
        final long index = getIndex(x, y, width);

        final int byteIndex = (int)(index >> 32);
        final int bitIndex = (int)(index & 0xFF);

        byteArray[byteIndex] |= (1 << bitIndex); 
    }
    

    public StoreOrder getByteOrder() {
        return byteOrder;
    }

    public StoreOrder getBitOrder() {
        return bitOrder;
    }

    public DataLength getPadding() {
        return padding;
    }

    public DataLength getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return "FontBitmapFormat [byteOrder=" + byteOrder + ", bitOrder=" + bitOrder + ", padding=" + padding
                + ", storage=" + storage + "]";
    }
}
