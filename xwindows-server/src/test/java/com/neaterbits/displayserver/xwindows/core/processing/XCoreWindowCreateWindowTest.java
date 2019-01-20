package com.neaterbits.displayserver.xwindows.core.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.events.CreateNotify;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.xwindows.core.util.XWindowAttributesBuilder;


public final class XCoreWindowCreateWindowTest extends BaseXCoreWindowTest {

    @Test
    public void testOpenAndCloseWindow() throws IDChoiceException {

        final Position position = new Position(150, 250);
        final Size size = new Size(350, 450);

        final WindowState window = checkCreateWindow(position, size);
        
        checkDestroyWindow(window);
    }

    @Test
    public void testCreateWindowNotify() throws IDChoiceException {
        
        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);

        final int borderWidth = 3;
        
        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_NOTIFY);
        
        whenEvent(CreateNotify.class);
        
        final WindowState window = checkCreateWindow(position, size, borderWidth, null, null, false);

        final CreateNotify createNotify = expectEvent(CreateNotify.class);

        verifyNoMoreInteractions();

        assertThat(createNotify).isNotNull();
        
        assertThat(createNotify.getParent()).isEqualTo(rootWindow);
        assertThat(createNotify.getWindow()).isEqualTo(window.windowResource);
        
        assertThat((int)createNotify.getX().getValue()).isEqualTo(position.getLeft());
        assertThat((int)createNotify.getY().getValue()).isEqualTo(position.getTop());
        assertThat((int)createNotify.getWidth().getValue()).isEqualTo(size.getWidth());
        assertThat((int)createNotify.getHeight().getValue()).isEqualTo(size.getHeight());
        assertThat((int)createNotify.getBorderWidth().getValue()).isEqualTo(borderWidth);
        assertThat(createNotify.getOverrideRedirect()).isEqualTo(BOOL.False);
    }

    @Test
    public void testCreateWindowNotifyOverrideRedirectTrue() throws IDChoiceException {
        
        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);

        final int borderWidth = 3;
        
        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_NOTIFY);
        
        whenEvent(CreateNotify.class);
        
        final XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setOverrideRedirect(BOOL.True)
                .build();
        
        assertThat(windowAttributes).isNotNull();
        
        assertThat(windowAttributes.isSet(XWindowAttributes.OVERRIDE_REDIRECT));
        
        final WindowState window = checkCreateWindow(position, size, borderWidth, windowAttributes, null, false);

        final CreateNotify createNotify = expectEvent(CreateNotify.class);

        verifyNoMoreInteractions();

        assertThat(createNotify).isNotNull();
        
        assertThat(createNotify.getParent()).isEqualTo(rootWindow);
        assertThat(createNotify.getWindow()).isEqualTo(window.windowResource);
        
        assertThat((int)createNotify.getX().getValue()).isEqualTo(position.getLeft());
        assertThat((int)createNotify.getY().getValue()).isEqualTo(position.getTop());
        assertThat((int)createNotify.getWidth().getValue()).isEqualTo(size.getWidth());
        assertThat((int)createNotify.getHeight().getValue()).isEqualTo(size.getHeight());
        assertThat((int)createNotify.getBorderWidth().getValue()).isEqualTo(borderWidth);
        assertThat(createNotify.getOverrideRedirect()).isEqualTo(BOOL.True);
    }

}
