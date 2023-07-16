package com.exithere.rain.dto.response;

import com.exithere.rain.entity.Alarm;
import com.exithere.rain.entity.enumeration.Ratio;
import com.exithere.rain.entity.enumeration.TargetDay;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class AlarmResponse {
    private int targetDay;
    private LocalTime targetTime;
    private List<Integer> targetDate;
    private boolean summery;
    private boolean special;
    private boolean rainFall;
    private int ratio;
    private boolean dust;

    public static AlarmResponse from(Alarm alarm){
        List<Integer> targetDate = new ArrayList<>();
        if(alarm.isSun()){
            targetDate.add(0);
        }
        if(alarm.isMon()){
            targetDate.add(1);
        }
        if(alarm.isTue()){
            targetDate.add(2);
        }
        if(alarm.isWed()){
            targetDate.add(3);
        }
        if(alarm.isThu()){
            targetDate.add(4);
        }
        if(alarm.isFri()){
            targetDate.add(5);
        }
        if(alarm.isSat()){
            targetDate.add(6);
        }

        return AlarmResponse.builder()
                .targetDay(alarm.getTargetDay().getIndex())
                .targetTime(alarm.getTargetTime())
                .targetDate(targetDate)
                .summery(alarm.isSummary())
                .special(alarm.isSpecial())
                .rainFall(alarm.isRainFall())
                .ratio(alarm.getRatio().getIndex())
                .dust(alarm.isDust())
                .build();
    }
}
