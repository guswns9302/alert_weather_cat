package com.exithere.rain.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegionSelectOrDeleteRequest {
    private String deviceId;
    private int regionId;
}
