package com.neaterbits.displayserver.util.logging;

import java.util.Objects;

public abstract class BaseLogImpl {
    private final String prefix;
    private final DebugLevel debugLevel;

    public BaseLogImpl(String prefix, DebugLevel debugLevel) {
        Objects.requireNonNull(prefix);

        this.prefix = prefix;
        this.debugLevel = debugLevel;
    }
    
    protected final void log(DebugLevel debugLevel, String event, Object ... parameters) {
        
        if (this.debugLevel == debugLevel) {
            System.out.print(prefix + '.' + event + '(');
            
            LogUtil.outputParameters(System.out::append, parameters);
            
            System.out.println(')');
        }
    }
    
    protected final void debug(String event, Object ... parameters) {
        log(DebugLevel.DEBUG, event, parameters);
    }

    protected final void trace(String event, Object ... parameters) {
        log(DebugLevel.TRACE, event, parameters);
    }
}
