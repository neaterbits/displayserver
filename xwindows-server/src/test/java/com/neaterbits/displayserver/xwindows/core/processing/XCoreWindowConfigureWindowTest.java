package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.neaterbits.displayserver.protocol.messages.replies.GetGeometryReply;
import com.neaterbits.displayserver.protocol.messages.requests.ConfigureWindow;
import com.neaterbits.displayserver.protocol.messages.requests.GetGeometry;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowConfiguration;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.util.XWindowConfigurationBuilder;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public class XCoreWindowConfigureWindowTest extends BaseXCorePixmapTest {

    @Test
    public void testConfigurePosition() {

        final Position position = new Position(150, 250);
        final Size size = new Size(350, 450);

        final WindowState window = checkCreateWindow(position, size);

        final XWindowConfiguration configuration = new XWindowConfigurationBuilder()
                .setX(new INT16((short)175))
                .setY(new INT16((short)275))
                .build();
        
        final ConfigureWindow configureWindow = new ConfigureWindow(window.windowResource, configuration);

        sendRequest(configureWindow);
        
        sendRequest(new GetGeometry(window.windowResource.toDrawable()));
        
        final GetGeometryReply getGeometryReply = expectReply(GetGeometryReply.class);

        assertThat(getGeometryReply).isNotNull();
        assertThat((int)getGeometryReply.getX().getValue()).isEqualTo(175);
        assertThat((int)getGeometryReply.getY().getValue()).isEqualTo(275);
    }
}
