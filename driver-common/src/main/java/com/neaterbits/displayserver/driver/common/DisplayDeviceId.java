package com.neaterbits.displayserver.driver.common;

import java.util.Objects;

public final class DisplayDeviceId {

    private final String deviceId;
    
    public DisplayDeviceId(String deviceId) {

        Objects.requireNonNull(deviceId);
        
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DisplayDeviceId other = (DisplayDeviceId) obj;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        return true;
    }
}
