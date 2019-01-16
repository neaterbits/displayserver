package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Arrays;

public abstract class BaseFontTest {

    protected final String getFontAliasesFile() {
        return "/etc/X11/fonts/misc/xfonts-base.alias";
    }
    
    protected final FontLoader getFontLoader(boolean fontAliases) {
     
        return new FontLoader(
            new FontLoaderConfig(
                    Arrays.asList("/usr/share/fonts/X11/misc"),
                    fontAliases ? getFontAliasesFile() : null));
    }
    
}
