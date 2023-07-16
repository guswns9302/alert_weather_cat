package com.exithere.rain.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString
public class AlarmRequest {

    private String deviceCd;
    private int targetDay;
    private LocalTime targetTime;
    private List<Integer> targetDate;
    private boolean summery;
    private boolean special;
    private boolean rainFall;
    private int ratio;
    private boolean dust;

}
