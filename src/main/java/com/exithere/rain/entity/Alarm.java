package com.exithere.rain.entity;

import com.exithere.rain.dto.request.AlarmRequest;
import com.exithere.rain.entity.enumeration.Ratio;
import com.exithere.rain.entity.enumeration.TargetDay;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

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
        this.targetDay = TargetDay.valueOf(alarmRequest.getTargetDay());
        this.targetTime = alarmRequest.getTargetTime();
        this.mon = alarmRequest.isMon();
        this.tue = alarmRequest.isTue();
        this.wed = alarmRequest.isWed();
        this.thu = alarmRequest.isThu();
        this.fri = alarmRequest.isFri();
        this.sat = alarmRequest.isSat();
        this.sun = alarmRequest.isSun();
        this.summary = alarmRequest.isSummery();
        this.special = alarmRequest.isSpecial();
        this.rainFall = alarmRequest.isRainFall();
        this.ratio = Ratio.valueOf(alarmRequest.getRatio());
        this.dust = alarmRequest.isDust();
    }
}
