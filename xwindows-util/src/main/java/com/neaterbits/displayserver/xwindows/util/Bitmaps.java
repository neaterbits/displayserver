package com.neaterbits.displayserver.xwindows.util;

import java.io.PrintStream;

public class Bitmaps {

    public static void printBitmap(PrintStream out, byte [] bitmap, int stride) {
        
        for (int i = 0; i < bitmap.length; ++ i) {

            if (i != 0 && i % stride == 0) {
                out.println();
            }

            // out.format("%02x", from[i]);
            
            final int characterUnsigned = Unsigned.byteToUnsigned(bitmap[i]);
            
            out.print(String.format("%8s", Integer.toBinaryString(characterUnsigned)).replace(' ', '0'));
        }
        
        out.println();
    }
}
