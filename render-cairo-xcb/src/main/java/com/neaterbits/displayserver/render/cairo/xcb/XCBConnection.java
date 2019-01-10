package com.neaterbits.displayserver.render.cairo.xcb;

public class XCBConnection extends XCBReference implements AutoCloseable {

    public static XCBConnection connect(String display) {
        final long connection = XCBNative.xcb_connect(display);
        
        return connection != 0L ? new XCBConnection(connection, display) : null;
    }

    public static XCBConnection connect(String display, String authMethod, byte [] authData) {
        
        final long connection = XCBNative.xcb_connect_display(display, authMethod, authData);
        
        return connection != 0L ? new XCBConnection(connection, display) : null;
    }
    
    private final String display;
    
    private XCBConnection(long reference, String display) {
        super(reference);
        
        this.display = display;
    }

    public XCBSetup getSetup() {
        
        final long setup = XCBNative.xcb_get_setup(getXCBReference());
        
        return setup != 0L ? new XCBSetup(setup) : null;
    }

    public int sendRequest(byte [] data, int opcode, boolean isvoid) {

        final int sequenceNumber = XCBNative.xcb_send_request(
            getXCBReference(),
            data,
            opcode,
            isvoid);
    
        if (sequenceNumber == 0) {
            throw new IllegalStateException();
        }
        
        return sequenceNumber;
    }
    
    public byte [] waitForReply(int sequenceNumber) {

        return XCBNative.xcb_wait_reply(getXCBReference(), sequenceNumber);
    }
    
    
    public void flush() {
        XCBNative.xcb_flush(getXCBReference());
    }
    
    @Override
    public void close() throws Exception {
        XCBNative.xcb_disconnect(getXCBReference());
    }

    @Override
    public void dispose() {
        try {
            close();
        } catch (Exception ex) {
        }
    }

    @Override
    public String toString() {
        return display + "/" + String.format("%08x", getReference());
    }
}
