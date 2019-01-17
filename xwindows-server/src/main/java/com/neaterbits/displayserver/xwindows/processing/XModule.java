package com.neaterbits.displayserver.xwindows.processing;

import java.util.Collection;

public abstract class XModule extends XMessageDispatcher {

    protected XModule(Collection<XOpCodeProcessor> opcodeProcessors) {
        super(opcodeProcessors);
    }
}
