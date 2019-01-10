package com.neaterbits.displayserver.render.cairo.xcb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class XCBSetup extends XCBReference {

    XCBSetup(long reference) {
        super(reference);
    }

    public int getSuccess() {
        return XCBNative.setup_status(getXCBReference());
    }

    public int getProtocolMajorVersion() {
        return XCBNative.setup_protocol_major_version(getXCBReference());
    }

    public int getProtocolMinorVersion() {
        return XCBNative.setup_protocol_minor_version(getXCBReference());
    }

    public long getReleaseNumber() {
        return XCBNative.setup_release_number(getXCBReference());
    }

    public long getResourceIdBase() {
        return XCBNative.setup_resource_id_base(getXCBReference());
    }

    public long getResourceIdMask() {
        return XCBNative.setup_resource_id_mask(getXCBReference());
    }

    public long getMotionBufferSize() {
        return XCBNative.setup_motion_buffer_size(getXCBReference());
    }

    public int getMaximumRequestLength() {
        return XCBNative.setup_maximum_request_length(getXCBReference());
    }

    public int getImageByteOrder() {
        return XCBNative.setup_image_byte_order(getXCBReference());
    }

    public int getBitmapFormatBitOrder() {
        return XCBNative.setup_bitmap_format_bit_order(getXCBReference());
    }

    public int getBitmapFormatScanlineUnit() {
        return XCBNative.setup_bitmap_format_scanline_unit(getXCBReference());
    }

    public int getBitmapFormatScanlinePad() {
        return XCBNative.setup_bitmap_format_scanline_pad(getXCBReference());
    }

    public int getMinKeyCode() {
        return XCBNative.setup_min_keycode(getXCBReference());
    }

    public int getMaxKeyCode() {
        return XCBNative.setup_max_keycode(getXCBReference());
    }

    public String getVendor() {
        return XCBNative.setup_vendor(getXCBReference());
    }

    public List<XCBFormat> getPixmapFormats() {
        
        final long [] formatReferences = XCBNative.setup_get_formats(getXCBReference());
        
        return Arrays.stream(formatReferences)
                .mapToObj(XCBFormat::new)
                .collect(Collectors.toList());
    }

    public List<XCBScreen> getScreens() {
        
        final long [] screenReferences = XCBNative.setup_get_screens(getXCBReference());
        
        return Arrays.stream(screenReferences)
                .mapToObj(XCBScreen::new)
                .collect(Collectors.toList());
    }

    @Override
    public void dispose() {
        
    }
}
