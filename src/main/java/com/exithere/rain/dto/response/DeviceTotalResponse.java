package com.exithere.rain.dto.response;

import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@Builder
public class DeviceTotalResponse {

    private String deviceId;
    private String fcmToken;
    private Region selectRegion;
    private ArrayList<Region> regionList;
    private boolean pushBtn;
    private boolean specialReport;
    private Alarm alarm;

    public static DeviceTotalResponse from(Device device){
        ArrayList<Region> regions = new ArrayList<>();
        if(device.getFirstRegionCd() != null){
            regions.add(device.getFirstRegionCd());
        }
        if(device.getSecondRegionCd() != null){
            regions.add(device.getSecondRegionCd());
        }
        if(device.getThirdRegionCd() != null){
            regions.add(device.getThirdRegionCd());
        }

        return DeviceTotalResponse.builder()
                .deviceId(device.getDeviceId())
                .fcmToken(device.getFcmToken())
                .selectRegion(device.getSelectRegionCd())
                .regionList(regions)
                .pushBtn(device.isPushBtn())
                .alarm(device.getAlarm())
                .specialReport(device.isSpecialReport())
                .build();
    }
}
