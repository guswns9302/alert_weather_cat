package com.exithere.rain.service;

import com.exithere.rain.dto.request.AlarmRequest;
import com.exithere.rain.dto.response.AlarmResponse;
import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import com.exithere.rain.entity.enumeration.Ratio;
import com.exithere.rain.entity.enumeration.TargetDay;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.AlarmRepository;
import com.exithere.rain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public Alarm defaultAlarmCreate(String deviceId){
        Alarm alarm = Alarm.builder()
                .deviceCd(deviceId)
                .targetDay(TargetDay.TODAY)
                .targetTime(LocalTime.of(7,0))
                .mon(true).tue(true).wed(true).thu(true).fri(true)
                .sat(false).sun(false)
                .summary(true)
                .special(false)
                .rainFall(false)
                .ratio(Ratio.SIXTH)
                .dust(false)
                .build();

        Alarm saveAlarm = alarmRepository.save(alarm);
        return saveAlarm;
    }

    @Transactional
    public AlarmResponse updateSetting(AlarmRequest alarmRequest) {
        Optional<Alarm> existAlarm = alarmRepository.findById(alarmRequest.getDeviceCd());

        if(existAlarm.isPresent()){
            existAlarm.get().updateAll(alarmRequest);
            return AlarmResponse.from(existAlarm.get());
        }
        else{
            throw new CustomException(ErrorCode.DEVICE_NOT_FOUND);
        }
    }
}
