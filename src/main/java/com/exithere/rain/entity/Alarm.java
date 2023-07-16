package com.exithere.rain.entity;

import com.exithere.rain.dto.request.AlarmRequest;
import com.exithere.rain.entity.enumeration.Ratio;
import com.exithere.rain.entity.enumeration.TargetDay;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.IntStream;

@Getter
@Builder
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "TB_ALARM")
public class Alarm {

    @Id
    @Column(name = "DEVICE_CD")
    private String deviceCd;

    @Column(name = "TARGET_DAY")
    @Enumerated(EnumType.STRING)
    private TargetDay targetDay;

    @Column(name = "TARGET_TIME")
    private LocalTime targetTime;

    @Column(name = "MON")
    private boolean mon;

    @Column(name = "TUE")
    private boolean tue;

    @Column(name = "WED")
    private boolean wed;

    @Column(name = "THU")
    private boolean thu;

    @Column(name = "FRI")
    private boolean fri;

    @Column(name = "SAT")
    private boolean sat;

    @Column(name = "SUN")
    private boolean sun;

    @Column(name = "SUMMARY")
    private boolean summary;

    @Column(name = "SPECIAL")
    private boolean special;

    @Column(name = "RAIN_FALL")
    private boolean rainFall;

    @Column(name = "RAIN_RATIO")
    @Enumerated(EnumType.STRING)
    private Ratio ratio;

    @Column(name = "DUST")
    private boolean dust;

    public void updateAll(AlarmRequest alarmRequest){
        this.mon = false;
        this.tue = false;
        this.wed = false;
        this.thu = false;
        this.fri = false;
        this.sat = false;
        this.sun = false;

        this.targetDay = TargetDay.find(alarmRequest.getTargetDay());
        this.targetTime = alarmRequest.getTargetTime();

        for(int index : alarmRequest.getTargetDate()){
            switch (index){
                case 0:
                    this.sun = true;
                    break;
                case 1:
                    this.mon = true;
                    break;
                case 2:
                    this.tue = true;
                    break;
                case 3:
                    this.wed = true;
                    break;
                case 4:
                    this.thu = true;
                    break;
                case 5:
                    this.fri = true;
                    break;
                case 6:
                    this.sat = true;
                    break;
            }
        }

        this.summary = alarmRequest.isSummery();
        this.special = alarmRequest.isSpecial();
        this.rainFall = alarmRequest.isRainFall();
        this.ratio = Ratio.find(alarmRequest.getRatio());
        this.dust = alarmRequest.isDust();
    }
}
