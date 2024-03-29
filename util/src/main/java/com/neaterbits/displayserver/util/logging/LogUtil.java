package com.neaterbits.displayserver.util.logging;

import java.util.function.Consumer;

public class LogUtil {

    public static String join(Iterable<String> iterable, String separator) {
        
        final StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        
        for (String string : iterable) {
            
            if (first) {
                first = false;
            }
            else {
                sb.append(separator);
            }

            sb.append(string);
        }

        return sb.toString();
    }
    
    public static Object [] wrap(Object ... objects) {
        return objects;
    }

    public static String outputParametersInBrackets(Object ... parameters) {
        
        final StringBuilder sb = new StringBuilder();
        
        outputParametersInBrackets(sb::append, parameters);
    
        return sb.toString();
    }

    public static void outputParametersInBrackets(Consumer<String> append, Object ... parameters) {
        append.accept("[");
        
        outputParameters(append, parameters);
    
        append.accept("]");
    }
    
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
            
            final Object value = parameters[i ++];
            
            append.accept(value != null ? value.toString() : "null");
        }
    }
}
