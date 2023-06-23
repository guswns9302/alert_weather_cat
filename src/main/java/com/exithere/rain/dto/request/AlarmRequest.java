package com.exithere.rain.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@ToString
public class AlarmRequest {

    private String deviceCd;
    private String targetDay;
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
    private String ratio;
    private boolean dust;

}
