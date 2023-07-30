package com.exithere.rain.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name="TB_ALARM_HISTORY")
public class AlarmHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALARM_ID")
    private Long alarmHistoryId;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "REGION_NAME")
    private String regionName;

    @Column(name = "PUSH_DATE_TIME")
    private LocalDateTime pushDateTime;

    @Column(name ="PUSH_TYPE")
    private int pushType;
    // 0 -> 날씨 요약
    // 1 -> 기상 특보
    // 2 -> 강수
    // 3 -> 미세먼지

    @Column(name = "TITLE")
    private String title;

    @Column(name = "TEMP")
    private String temp;

    @Column(name = "POP")
    private int pop;

    @Column(name = "DUST")
    private String dust;

    @Column(name = "ULTRA_DUST")
    private String ultraDust;
}
