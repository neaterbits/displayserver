package com.neaterbits.displayserver.xwindows.core.processing;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.xwindows.model.XGC;

public abstract class BaseXCoreGCTest extends BaseXCorePixmapTest {

    static class GCState {
        final GCONTEXT gc;
        final XGC xgc;

        GCState(GCONTEXT gc, XGC xgc) {
            this.gc = gc;
            this.xgc = xgc;
        }
    }
    
    protected final GCState checkCreateGC(DRAWABLE drawable, XGCAttributes attributes) {

        final GCONTEXT gc = new GCONTEXT(allocateResourceId());

        final CreateGC createGC = new CreateGC(gc, drawable, attributes);
        
        sendRequest(createGC);

        try {
            verify(client).createGC(isNotNull());
        } catch (IDChoiceException ex) {
            throw new IllegalStateException(ex);
        }

        final XGC xgc = new XGC(attributes);

        return new GCState(gc, xgc);
    }

    protected final void whenGetGCFromClient(GCState gcState) {
        try {
            when(client.getGC(eq(gcState.gc))).thenReturn(gcState.xgc);
        } catch (GContextException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected final void verifyGetGCFromClient(GCState gcState) {
        try {
            verify(client).getGC(eq(gcState.gc));
        } catch (GContextException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
