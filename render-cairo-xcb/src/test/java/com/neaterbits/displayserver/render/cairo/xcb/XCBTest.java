package com.neaterbits.displayserver.render.cairo.xcb;

import org.junit.Before;
import org.junit.Test;

import com.neaterbits.displayserver.xwindows.util.XAuth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class XCBTest {

    public static void load() {
        System.load(System.getenv("HOME") + "/projects/displayserver/native-xcb/Debug/libxcbjni.so");
    }

    @Before
    public void loadXCB() {
        load();
    }

    @Test
    public void testConnect() throws Exception {
        
        final int display = 1;
        
        final XAuth xAuth = XAuth.getXAuthInfo(display, "MIT-MAGIC-COOKIE-1");

        final XCBConnection connection = XCBConnection.connect(":" + display, xAuth.getAuthorizationProtocol(), xAuth.getAuthorizationData());
        
        assertThat(connection).isNotNull();
        
        final XCBSetup setup = connection.getSetup();
        
        assertThat(setup).isNotNull();
        
        final List<XCBScreen> screens = setup.getScreens();
        
        assertThat(screens.size()).isEqualTo(1);
     
        final XCBScreen screen = screens.get(0);
        
        final List<XCBDepth> depths = screen.getDepths();
        
        assertThat(depths.isEmpty()).isFalse();
        
        for (XCBDepth depth : depths) {
            System.out.println("Depth: " + depth.getDepth());
            
            final List<XCBVisual> visuals = depth.getVisuals();
            
            System.out.println("Visuals: " + visuals);
        }
    }

    // @Test
    public void testWindowRender() throws Exception {
        
        final int display = 2;
        
        // final XAuth xAuth = XAuth.getXAuthInfo(display, "MIT-MAGIC-COOKIE-1");

        // final XCBConnection connection = XCBConnection.connect(":" + display, xAuth.getAuthorizationProtocol(), xAuth.getAuthorizationData());
        final XCBConnection connection = XCBConnection.connect(":" + display);
        
        assertThat(connection).isNotNull();

        System.out.println("## call test");
        
        XCBNative.test(connection.getXCBReference());
        
        System.out.println("## test done");

        connection.close();
    }
}
