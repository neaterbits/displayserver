package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class XCoreWindowTest extends BaseXCoreWindowTest {

    @Test
    public void testOpenAndCloseWindow() throws IDChoiceException {

        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);

        final WindowState window = checkCreateWindow(position, size);
        
        checkDestroyWindow(window);
    }
}
