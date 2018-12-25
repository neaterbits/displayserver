package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class TOC {

    private final List<TOCEntry> entries;

    TOC(List<TOCEntry> entries) {
        
        final ArrayList<TOCEntry> list = new ArrayList<>(entries);
        
        Collections.sort(list, (entry1, entry2) -> Integer.compare(entry1.getOffset(), entry2.getOffset()));
        
        this.entries = Collections.unmodifiableList(list);
    }

    List<TOCEntry> getEntriesSorted() {
        return entries;
    }
}
