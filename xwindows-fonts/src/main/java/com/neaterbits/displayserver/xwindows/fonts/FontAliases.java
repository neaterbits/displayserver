package com.neaterbits.displayserver.xwindows.fonts;

import java.util.List;
import java.util.Map;
import java.util.Objects;

final class FontAliases {

    private final Map<String, XLFD> fontNameToXLFD;
    private final List<XLFDAlias> xlfdAliases;
    
    FontAliases(Map<String, XLFD> fontNameToXLFD, List<XLFDAlias> xlfdAliases) {

        Objects.requireNonNull(fontNameToXLFD);
        Objects.requireNonNull(xlfdAliases);
        
        this.fontNameToXLFD = fontNameToXLFD;
        this.xlfdAliases = xlfdAliases;
    }

    void getFontNamesForXLFD(XLFD xlfd, List<FontDescriptor> fontNames) {
        
        getFontNameForXLFDFromMap(xlfd, fontNames);
        
        for (XLFDAlias xlfdAlias : xlfdAliases) {
            if (xlfd.matchesFont(xlfdAlias.getFrom())) {
                getFontNameForXLFDFromMap(xlfdAlias.getTo(), fontNames);
            }
        }
    }

    private void getFontNameForXLFDFromMap(XLFD xlfd, List<FontDescriptor> fontNames) {

        for (Map.Entry<String, XLFD> entry : fontNameToXLFD.entrySet()) {
            if (xlfd.matchesFont(entry.getValue())) {
                
                final String fontName = entry.getKey();
                
                if (!fontNames.contains(fontName)) {
                    fontNames.add(new FontDescriptor(fontName, entry.getValue()));
                }
            }
        }
        
    }

    @Override
    public String toString() {
        return "FontAliases [fontNameToXLFD=" + fontNameToXLFD + ", xlfdAliases=" + xlfdAliases + "]";
    }
}
