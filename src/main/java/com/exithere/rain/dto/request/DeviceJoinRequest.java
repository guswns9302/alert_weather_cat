package com.exithere.rain.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceJoinRequest {

    private String deviceId;
    private String fcmToken;
    private String regionName;
    private int regionX;
    private int regionY;

}
