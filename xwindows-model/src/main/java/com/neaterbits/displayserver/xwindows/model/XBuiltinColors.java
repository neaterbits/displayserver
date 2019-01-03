package com.neaterbits.displayserver.xwindows.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XBuiltinColors {

    private final Map<String, XBuiltinColor> colorByName;

    public static XBuiltinColors decode(InputStream inputStream) throws IOException {
        
        final Map<String, XBuiltinColor> colorByName = new HashMap<>();
        
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line;
        
        while (null != (line = reader.readLine())) {
            final String trimmed = line.trim();
            
            if (!trimmed.isEmpty()) {
                
                final String [] strings = trimmed.split("\\s+");
                
                if (strings.length >= 4) {

                    final int r, g, b;
                    
                    try {
                        r = Integer.parseInt(strings[0]);
                        g = Integer.parseInt(strings[1]);
                        b = Integer.parseInt(strings[2]);
                    }
                    catch (NumberFormatException ex) {
                        continue;
                    }

                    final StringBuilder name = new StringBuilder();
                    
                    for (int i = 3; i < strings.length; ++ i) {
                        
                        if (i > 3) {
                            name.append(' ');
                        }
                        
                        name.append(strings[i]);
                    }
                    
                    final XBuiltinColor color = new XBuiltinColor(r, g, b);
                    
                    colorByName.put(name.toString().toLowerCase(), color);
                }
            }
        }
        
        return new XBuiltinColors(colorByName);
    }
    
    private XBuiltinColors(Map<String, XBuiltinColor> colorByName) {
        this.colorByName = colorByName;
    }

    public XBuiltinColor getColor(String name) {
        Objects.requireNonNull(name);
        
        return colorByName.get(name.toLowerCase());
    }
}
