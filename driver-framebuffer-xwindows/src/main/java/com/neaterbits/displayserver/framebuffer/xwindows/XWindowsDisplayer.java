package com.neaterbits.displayserver.framebuffer.xwindows;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.common.DisplayDeviceId;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.framebuffer.common.DisplayDevice;
import com.neaterbits.displayserver.framebuffer.common.DisplayMode;
import com.neaterbits.displayserver.framebuffer.common.Encoder;
import com.neaterbits.displayserver.framebuffer.common.OutputConnector;
import com.neaterbits.displayserver.framebuffer.common.RenderingProvider;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

/**
 * Combination of framebuffer and display device, since XWindows WINDOW datatype is both
 *
 */

final class XWindowsDisplayer extends XWindowsRenderingProvider implements RenderingProvider {
    
    private final SCREEN screen;
    
    private final OutputConnector outputConnector;
    private final DisplayDevice displayDevice;
    
    private static int getDefaultScreenNo() {
        return 0;
    }
    
    private static SCREEN getDefaultScreen(XWindowsDriverConnection driverConnection) {
        final ServerMessage serverMessage = driverConnection.getServerMessage();
        
        Objects.requireNonNull(serverMessage);
        
        return serverMessage.getScreens()[getDefaultScreenNo()];
    }

    private static int getDepth(XWindowsDriverConnection driverConnection) {
        return getDefaultScreen(driverConnection).getRootDepth().getValue();
    }
    
    XWindowsDisplayer(
            XWindowsDriverConnection driverConnection,
            Position position,
            Size size,
            DisplayDeviceId displayDeviceId) throws IOException {
        
        super(
                driverConnection,
                getDefaultScreenNo(),
                XWindowsClientHelper.createWindow(
                        driverConnection,
                        position,
                        size,
                        getDefaultScreen(driverConnection)),
                size,
                getDepth(driverConnection));

        Objects.requireNonNull(driverConnection);
        Objects.requireNonNull(size);
        Objects.requireNonNull(displayDeviceId);
        
        this.screen = getDefaultScreen(driverConnection);
        
        this.displayDevice = new DisplayDevice() {
            
            @Override
            public Size getSizeInMillimeters() {
                return new Size(
                        dimensionInMillimeters(size.getWidth(), screen.getWidthInPixels().getValue(), screen.getWidthInMillimiters().getValue()),
                        dimensionInMillimeters(size.getHeight(), screen.getHeightInPixels().getValue(), screen.getHeightInMillimiters().getValue()));
            }
            
            @Override
            public DisplayDeviceId getDisplayDeviceId() {
                return displayDeviceId;
            }
            
            @Override
            public List<DisplayMode> getAvailableModes() {
                return Arrays.asList(new DisplayMode(size, getDepth(driverConnection), 75.0f));
            }
        };
        
        this.outputConnector = new OutputConnector() {
            
            @Override
            public DisplayDevice getConnectedDisplay() {
                return displayDevice;
            }
        };
    }
    
    DisplayDevice getDisplayDevice() {
        return displayDevice;
    }
    
    OutputConnector getOutputConnector() {
        return outputConnector;
    }
    
    @Override
    public final List<Encoder> getEncoders() {
        return Arrays.asList(new Encoder() {

            @Override
            public boolean mayOutputTo(DisplayDevice displayDevice) {
                return true;
            }

            @Override
            public boolean supports(OutputConnector connector) {
                return true;
            }
        });
    }

    @Override
    public final List<OutputConnector> getOutputConnectors() {
        return Arrays.asList(outputConnector);
    }

    private static int dimensionInMillimeters(int windowInPixels, int screenInPixels, int screenInMillimeters) {
        final double ratio = windowInPixels / (double)screenInPixels;
    
        return (int)(screenInMillimeters * ratio);
    }
}
