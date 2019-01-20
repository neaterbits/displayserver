package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.KEYCODE;

public final class ServerMessage extends XEncodeable {
	
	private final BYTE success;
	
	private final CARD16 protocolMajorVersion;
	private final CARD16 protocolMinorVersion;
	
	// private final CARD16 length;
	
	private final CARD32 releaseNumber;
	private final CARD32 resourceIdBase;
	private final CARD32 resourceIdMask;
	private final CARD32 motionBufferSize;
	
	// private final CARD16 vendorLength;
	private final CARD16 maximumRequestLength;
	
	// private final CARD8 numberOfScreens;
	
	// private final CARD8 numberOfFormats;
	
	private final BYTE imageByteOrder;
	private final BYTE bitmapFormatBitOrder;
	
	
	private final CARD8 bitmapFormatScanlineUnit;
	private final CARD8 bitmapFormatScanlinePad;
	
	private final KEYCODE minKeyCode;
	private final KEYCODE maxKeyCode;
	
	private final String vendor;
	
	private final FORMAT [] pixmapFormats;
	private final SCREEN [] screens;
	
	public static ServerMessage decode(XWindowsProtocolInputStream stream) throws IOException {

	    final BYTE success = stream.readBYTE();
	    
	    stream.readBYTE();
	    
	    final CARD16 protocolMajorVersion = stream.readCARD16();
	    final CARD16 protocolMinorVersion = stream.readCARD16();
	    
	    /* final CARD16 length =  */ stream.readCARD16();
	    
	    final CARD32 releaseNumber = stream.readCARD32();
	    final CARD32 resourceIdBase = stream.readCARD32();
	    final CARD32 resourceIdMask = stream.readCARD32();
	    final CARD32 motionBufferSize = stream.readCARD32();
	    
	    final CARD16 vendorLength = stream.readCARD16();
	    
	    final CARD16 maximumRequestLength = stream.readCARD16();
	    
	    final CARD8 numberOfScreens = stream.readCARD8();
	    
	    final CARD8 numberOfFormats = stream.readCARD8();
	    
	    final BYTE imageByteOrder = stream.readBYTE();
	    final BYTE bitmapFormatBitOrder = stream.readBYTE();

	    final CARD8 bitmapFormatScanlineUnit = stream.readCARD8();
	    final CARD8 bitmapFormatScanlinePad = stream.readCARD8();
	    
	    final KEYCODE minKeyCode = stream.readKEYCODE();
	    final KEYCODE maxKeyCode = stream.readKEYCODE();

	    stream.readCARD32();
	    
	    final String vendor = stream.readSTRING8(vendorLength.getValue());
	    
	    stream.readPad(XWindowsProtocolUtil.getPadding(vendorLength.getValue()));
	    
	    final FORMAT [] pixmapFormats = new FORMAT[numberOfFormats.getValue()];

	    for (int i = 0; i < pixmapFormats.length; ++ i) {
	        pixmapFormats[i] = FORMAT.decode(stream);
	    }
	    
	    final SCREEN [] screens = new SCREEN[numberOfScreens.getValue()];
	    
	    for (int i = 0; i < screens.length; ++ i) {
	        screens[i] = SCREEN.decode(stream);
	    }

	    return new ServerMessage(
	            success,
	            protocolMajorVersion, protocolMinorVersion,
	            releaseNumber,
	            resourceIdBase, resourceIdMask,
	            motionBufferSize,
	            maximumRequestLength,
	            imageByteOrder, bitmapFormatBitOrder, bitmapFormatScanlineUnit, bitmapFormatScanlinePad,
	            minKeyCode, maxKeyCode,
	            vendor,
	            pixmapFormats,
	            screens);
	}
	
	public ServerMessage(
			BYTE success,
			
			CARD16 protocolMajorVersion,
			CARD16 protocolMinorVersion,
			
			// CARD16 length,
			CARD32 releaseNumber,
			
			CARD32 resourceIdBase,
			CARD32 resourceIdMask,
			CARD32 motionBufferSize,
			
			// CARD16 vendorLength,
			CARD16 maximumRequestLength,
			// CARD8 numberOfScreens,
			// CARD8 numberOfFormats,
			BYTE imageByteOrder,
			BYTE bitmapFormatBitOrder,
			CARD8 bitmapFormatScanlineUnit,
			CARD8 bitmapFormatScanlinePad,
			
			KEYCODE minKeyCode,
			KEYCODE maxKeyCode,
			String vendor,
			FORMAT[] pixmapFormats,
			SCREEN[] screens) {

		this.success = success;
		this.protocolMajorVersion = protocolMajorVersion;
		this.protocolMinorVersion = protocolMinorVersion;
		// this.length = length;
		this.releaseNumber = releaseNumber;
		this.resourceIdBase = resourceIdBase;
		this.resourceIdMask = resourceIdMask;
		this.motionBufferSize = motionBufferSize;
		// this.vendorLength = vendorLength;
		this.maximumRequestLength = maximumRequestLength;
		// this.numberOfScreens = numberOfScreens;
		// this.numberOfFormats = numberOfFormats;
		this.imageByteOrder = imageByteOrder;
		this.bitmapFormatBitOrder = bitmapFormatBitOrder;
		this.bitmapFormatScanlineUnit = bitmapFormatScanlineUnit;
		this.bitmapFormatScanlinePad = bitmapFormatScanlinePad;
		this.minKeyCode = minKeyCode;
		this.maxKeyCode = maxKeyCode;
		this.vendor = vendor;
		this.pixmapFormats = pixmapFormats;
		this.screens = screens;
	}

	public BYTE getSuccess() {
		return success;
	}

	public CARD16 getProtocolMajorVersion() {
		return protocolMajorVersion;
	}

	public CARD16 getProtocolMinorVersion() {
		return protocolMinorVersion;
	}

	/*
	public CARD16 getLength() {
		return length;
	}
	*/

	public CARD32 getReleaseNumber() {
		return releaseNumber;
	}

	public CARD32 getResourceIdBase() {
		return resourceIdBase;
	}

	public CARD32 getResourceIdMask() {
		return resourceIdMask;
	}

	public CARD32 getMotionBufferSize() {
		return motionBufferSize;
	}

	/*
	public CARD16 getVendorLength() {
		return vendorLength;
	}
	*/

	public CARD16 getMaximumRequestLength() {
		return maximumRequestLength;
	}

	/*
	public CARD8 getNumberOfScreens() {
		return numberOfScreens;
	}

	public CARD8 getNumberOfFormats() {
		return numberOfFormats;
	}
	*/

	public BYTE getImageByteOrder() {
		return imageByteOrder;
	}

	public BYTE getBitmapFormatBitOrder() {
		return bitmapFormatBitOrder;
	}

	public CARD8 getBitmapFormatScanlineUnit() {
		return bitmapFormatScanlineUnit;
	}

	public CARD8 getBitmapFormatScanlinePad() {
		return bitmapFormatScanlinePad;
	}

	public KEYCODE getMinKeyCode() {
		return minKeyCode;
	}

	public KEYCODE getMaxKeyCode() {
		return maxKeyCode;
	}

	public String getVendor() {
		return vendor;
	}

	public FORMAT[] getPixmapFormats() {
		return pixmapFormats;
	}

	public SCREEN[] getScreens() {
		return screens;
	}

	@Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
		
		stream.writeBYTE(success);
		stream.writeBYTE(new BYTE((byte)0));
		
		stream.writeCARD16(protocolMajorVersion);
		stream.writeCARD16(protocolMinorVersion);
		
        final int vendorAndScreenBytes = 
                vendor.length()
              + XWindowsProtocolUtil.getPadding(vendor.length())
              + length(screens);
        
        final int length = 
                  8 
                + 2 * pixmapFormats.length
                + (vendorAndScreenBytes / 4);

		stream.writeCARD16(new CARD16(length));
		
		stream.writeCARD32(releaseNumber);
		stream.writeCARD32(resourceIdBase);
		stream.writeCARD32(resourceIdMask);
		stream.writeCARD32(motionBufferSize);

		stream.writeCARD16(new CARD16(vendor.length()));
		stream.writeCARD16(maximumRequestLength);
		
		
		stream.writeCARD8(new CARD8((short)screens.length));
		stream.writeCARD8(new CARD8((short)pixmapFormats.length));
		
		stream.writeBYTE(imageByteOrder);
		stream.writeBYTE(bitmapFormatBitOrder);
		stream.writeCARD8(bitmapFormatScanlineUnit);
		stream.writeCARD8(bitmapFormatScanlinePad);
		
		stream.writeKEYCODE(minKeyCode);
		stream.writeKEYCODE(maxKeyCode);
		
		stream.writeCARD32(new CARD32(0));
		
		stream.writeSTRING8(vendor);
		
		stream.pad(XWindowsProtocolUtil.getPadding(vendor.length()));
		
		encodeArray(pixmapFormats, stream);
		encodeArray(screens, stream);
	}

    private static int length(SCREEN [] screens) {
        
        int length = 0;
        
        for (SCREEN screen : screens) {
            length += 40 + length(screen.getAllowedDepths());
        }
        
        return length;
    }
    
    private static int length(DEPTH [] depths) {

        int length = 0;

        for (DEPTH depth : depths) {
            length += 8 + depth.getVisuals().length * 24;
        }
    
        return length;
    }

    @Override
    public String toString() {
        return "ServerMessage [success=" + success + ", protocolMajorVersion=" + protocolMajorVersion
                + ", protocolMinorVersion=" + protocolMinorVersion + /* ", length=" + length + */ ", releaseNumber="
                + releaseNumber + ", resourceIdBase=" + hex(resourceIdBase) + ", resourceIdMask=" + hex(resourceIdMask)
                + ", motionBufferSize=" + motionBufferSize /* + ", vendorLength=" + vendorLength */
                + ", maximumRequestLength=" + maximumRequestLength + /* ", numberOfScreens=" + numberOfScreens */
                /* + ", numberOfFormats=" + numberOfFormats + */ ", imageByteOrder=" + imageByteOrder
                + ", bitmapFormatBitOrder=" + bitmapFormatBitOrder + ", bitmapFormatScanlineUnit="
                + bitmapFormatScanlineUnit + ", bitmapFormatScanlinePad=" + bitmapFormatScanlinePad + ", minKeyCode="
                + minKeyCode + ", maxKeyCode=" + maxKeyCode + ", vendor=" + vendor + ", pixmapFormats="
                + Arrays.toString(pixmapFormats) + ", screens=" + Arrays.toString(screens) + "]";
    }

    private static String hex(CARD32 card32) {
        return String.format("%08x", card32.getValue());
    }
}

