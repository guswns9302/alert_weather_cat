package com.exithere.rain.dto.Fcm;

import com.exithere.rain.dto.response.AlarmResponse;
import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import lombok.*;

import java.lang.annotation.Target;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetDto {

    private String deviceId;
    private String fcmToken;
    private Region selectRegion;
    private boolean pushBtn;
    private boolean specialReport;
    private AlarmResponse alarmResponse;

    public static TargetDto from(Device device){
        return TargetDto.builder()
                .deviceId(device.getDeviceId())
                .fcmToken(device.getFcmToken())
                .selectRegion(device.getSelectRegionCd())
                .pushBtn(device.isPushBtn())
                .specialReport(device.isSpecialReport())
                .alarmResponse(AlarmResponse.from(device.getAlarm()))
                .build();
    }
}
