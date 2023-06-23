package com.exithere.rain.dto.response;

import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.enumeration.Ratio;
import com.exithere.rain.entity.enumeration.TargetDay;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@ToString
@Builder
public class AlarmResponse {
    private TargetDay targetDay;
    private LocalTime targetTime;
    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    private boolean sat;
    private boolean sun;
    private boolean summery;
    private boolean special;
    private boolean rainFall;
    private Ratio ratio;
    private boolean dust;

    public static AlarmResponse from(Alarm alarm){
        return AlarmResponse.builder()
                .targetDay(alarm.getTargetDay())
                .targetTime(alarm.getTargetTime())
                .mon(alarm.isMon())
                .tue(alarm.isTue())
                .wed(alarm.isWed())
                .thu(alarm.isThu())
                .fri(alarm.isFri())
                .sat(alarm.isSat())
                .sun(alarm.isSun())
                .summery(alarm.isSummary())
                .special(alarm.isSpecial())
                .rainFall(alarm.isRainFall())
                .ratio(alarm.getRatio())
                .dust(alarm.isDust())
                .build();
    }
}
