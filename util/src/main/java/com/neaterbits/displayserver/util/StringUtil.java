package com.neaterbits.displayserver.util;

import java.io.PrintStream;

public class StringUtil {

    public static void indent(PrintStream out, int count) {
        
        out.print(indent(count));
        
    }

    public static String indent(int count) {
        
        final StringBuilder sb = new StringBuilder();
        
        indent(sb, count);
        
        return sb.toString();
    }

    public static void indent(StringBuilder sb, int count) {

        for (int i = 0; i < count; ++ i) {
            sb.append("  ");
        }
    }
}
