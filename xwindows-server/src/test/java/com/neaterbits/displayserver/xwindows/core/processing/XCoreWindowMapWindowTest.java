package com.neaterbits.displayserver.xwindows.core.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.events.MapNotify;
import com.neaterbits.displayserver.protocol.messages.events.MapRequest;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.core.util.XWindowAttributesBuilder;

public class XCoreWindowMapWindowTest extends BaseXCoreWindowTest {

    private void setOverrideRedirect(WINDOW window, BOOL value) {
        
        final XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setOverrideRedirect(value)
                .build();
        
        final ChangeWindowAttributes changeWindowAttributes = new ChangeWindowAttributes(window, windowAttributes);
        
        sendRequest(changeWindowAttributes);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindow() throws IDChoiceException {

        final WindowState window = checkCreateWindow();

        final MapWindow mapWindow = new MapWindow(window.windowResource);
    
        sendRequest(mapWindow);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureRedirect() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_REDIRECT);

        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);
        
        MapWindow mapWindow = new MapWindow(window.windowResource);
    
        whenEvent(MapRequest.class);
        
        sendRequest(mapWindow);

        final MapRequest mapRequest = expectEvent(MapRequest.class);
        
        assertThat(mapRequest).isNotNull();
    
        assertThat(mapRequest.getParent()).isEqualTo(rootWindow);
        assertThat(mapRequest.getWindow()).isEqualTo(window.windowResource);
        
        verifyNoMoreInteractions();

        Mockito.reset(client);
        
        // Map window again to see that is mapped
        subscribeEvents(rootWindow, 0);
     
        mapWindow = new MapWindow(window.windowResource);
        
        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureRedirectWhenOverride() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_REDIRECT);
        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);
        
        setOverrideRedirect(window.windowResource, BOOL.True);
        
        MapWindow mapWindow = new MapWindow(window.windowResource);
    
        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);

        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.True);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureNotify() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_NOTIFY);

        MapWindow mapWindow = new MapWindow(window.windowResource);

        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(rootWindow);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowNotify() {
        
        final WindowState window = checkCreateWindow();

        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);

        MapWindow mapWindow = new MapWindow(window.windowResource);

        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }
    
}
