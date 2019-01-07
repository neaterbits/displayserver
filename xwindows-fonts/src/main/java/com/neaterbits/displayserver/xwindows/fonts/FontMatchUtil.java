package com.neaterbits.displayserver.xwindows.fonts;

import java.util.regex.Pattern;

import com.neaterbits.displayserver.protocol.exception.ValueException;

class FontMatchUtil {

    static Pattern getFontMatchGlobPattern(String pattern) throws ValueException {

        if (!pattern.chars()
                .allMatch(ch -> 
                       Character.isLetterOrDigit(ch)
                    || ch == '-'
                    || ch == '_'
                    || ch == '?'
                    || ch == '*'
                    || ch == '.')) {
            
            throw new ValueException("Not a valid pattern", 0L);
        }

        final String lowercasePattern = pattern.toLowerCase().replace('?', '.').replace("*", ".*");

        final Pattern regexPattern = Pattern.compile(lowercasePattern);
        
        return regexPattern;
    }
}
