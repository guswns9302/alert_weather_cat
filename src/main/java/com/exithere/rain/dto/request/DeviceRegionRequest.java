package com.exithere.rain.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceRegionRequest {
    private String deviceId;
    private String regionName;
    private int regionX;
    private int regionY;
}
