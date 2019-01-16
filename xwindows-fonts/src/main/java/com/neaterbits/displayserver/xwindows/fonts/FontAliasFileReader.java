package com.neaterbits.displayserver.xwindows.fonts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FontAliasFileReader {

    public static FontAliases read(InputStream inputStream) throws IOException {
        
        final Map<String, XLFD> map = new HashMap<>();
        final List<XLFDAlias> xlfdAliases = new ArrayList<>();
        
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String line;
        
        while (null != (line = reader.readLine())) {
            
            final String trimmed = line.trim();
            
            if (!trimmed.isEmpty()) {
                
                final String [] parts = trimmed.split(" ");
                
                if (parts.length >= 2) {
                    
                    XLFD fromXlfd = null;
                    XLFD toXlfd = null;
                    
                    try {
                        try {
                            fromXlfd = XLFD.decode(parts[0], true);
                        }
                        catch (XFLDException ex) {
                        }
                        
                        toXlfd = XLFD.decode(parts[parts.length - 1], fromXlfd != null);
                    } catch (XFLDException ex) {
                    }
                    
                    if (fromXlfd != null && toXlfd != null) {
                        xlfdAliases.add(new XLFDAlias(fromXlfd, toXlfd));
                    }
                    else if (toXlfd != null) {
                        
                        final String fontName = parts[0];

                        if (!fontName.isEmpty()) {
                            map.put(fontName, toXlfd);
                        }
                    }
                }
            }
        }
        
        return new FontAliases(map, xlfdAliases);
    }
}
