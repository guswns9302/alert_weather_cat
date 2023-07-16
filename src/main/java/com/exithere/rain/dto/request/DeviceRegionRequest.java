package com.exithere.rain.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegionRequest {
    private String deviceId;
    private String regionName;
    private int regionX;
    private int regionY;
}
