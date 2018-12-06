package com.neaterbits.displayserver.util.logging;

import java.util.function.Consumer;

public class LogUtil {

    public static void outputParameters(Consumer<String> append, Object ... parameters) {
        
        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < parameters.length;) {
            
            if (i > 0) {
                append.accept(" ");
            }
            
            append.accept(parameters[i ++].toString());
            append.accept("=");
            append.accept(parameters[i ++].toString());
        }
    }
}
