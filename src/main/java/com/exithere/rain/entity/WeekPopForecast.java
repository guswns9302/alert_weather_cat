package com.exithere.rain.entity;

import com.exithere.rain.dto.response.forecast.pop.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="TB_WEEK_POP_FORECAST",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"REGION_ID", "BASE_TIME", "FORECAST_DATE"}
                )
        }
)
public class WeekPopForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WF_POP_ID")
    private Long wfPopId;

    @Column(name = "REGION_ID")
    private String regionId;

    @Column(name = "FORECAST_DATE")
    private LocalDate forecastDate;

    @Column(name = "BASE_TIME")
    private String baseTime;

    @Column(name = "RN_ST3_AM")
    private String rnSt3Am;

    @Column(name = "RN_ST3_PM")
    private String rnSt3Pm;

    @Column(name = "RN_ST4_AM")
    private String rnSt4Am;

    @Column(name = "RN_ST4_PM")
    private String rnSt4Pm;

    @Column(name = "RN_ST5_AM")
    private String rnSt5Am;

    @Column(name = "RN_ST5_PM")
    private String rnSt5Pm;

    @Column(name = "RN_ST6_AM")
    private String rnSt6Am;

    @Column(name = "RN_ST6_PM")
    private String rnSt6Pm;

    @Column(name = "RN_ST7_AM")
    private String rnSt7Am;

    @Column(name = "RN_ST7_PM")
    private String rnSt7Pm;

    @Column(name = "RN_ST8")
    private String rnSt8;

    @Column(name = "RN_ST9")
    private String rnSt9;

    @Column(name = "RN_ST10")
    private String rnSt10;

    @Column(name = "WF3_AM")
    private String wf3Am;

    @Column(name = "WF3_PM")
    private String wf3Pm;

    @Column(name = "WF4_AM")
    private String wf4Am;

    @Column(name = "WF4_PM")
    private String wf4Pm;

    @Column(name = "WF5_AM")
    private String wf5Am;

    @Column(name = "WF5_PM")
    private String wf5Pm;

    @Column(name = "WF6_AM")
    private String wf6Am;

    @Column(name = "WF6_PM")
    private String wf6Pm;

    @Column(name = "WF7_AM")
    private String wf7Am;

    @Column(name = "WF7_PM")
    private String wf7Pm;

    @Column(name = "WF8")
    private String wf8;

    @Column(name = "WF9")
    private String wf9;

    @Column(name = "WF10")
    private String wf10;

    public void updateWeekPop(String tmFc, Item item) {
        this.baseTime = tmFc;
        this.forecastDate = LocalDate.now();
        this.rnSt3Am = item.getRnSt3Am();
        this.rnSt3Pm = item.getRnSt3Pm();
        this.rnSt4Am = item.getRnSt4Am();
        this.rnSt4Pm = item.getRnSt4Pm();
        this.rnSt5Am = item.getRnSt5Am();
        this.rnSt5Pm = item.getRnSt5Pm();
        this.rnSt6Am = item.getRnSt6Am();
        this.rnSt6Pm = item.getRnSt6Pm();
        this.rnSt7Am = item.getRnSt7Am();
        this.rnSt7Pm = item.getRnSt7Pm();
        this.rnSt8 = item.getRnSt8();
        this.rnSt9 = item.getRnSt9();
        this.rnSt10 = item.getRnSt10();
        this.wf3Am = item.getWf3Am();
        this.wf3Pm = item.getWf3Pm();
        this.wf4Am = item.getWf4Am();
        this.wf4Pm = item.getWf4Pm();
        this.wf5Am = item.getWf5Am();
        this.wf5Pm = item.getWf5Pm();
        this.wf6Am = item.getWf6Am();
        this.wf6Pm = item.getWf6Pm();
        this.wf7Am = item.getWf7Am();
        this.wf7Pm = item.getWf7Pm();
        this.wf8 = item.getWf8();
        this.wf9 = item.getWf9();
        this.wf10 = item.getWf10();
    }
}
