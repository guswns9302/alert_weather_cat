package com.exithere.rain.entity;

import com.exithere.rain.dto.response.forecast.week.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="TB_WEEK_FORECAST",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"REGION_ID", "BASE_TIME", "FORECAST_DATE"}
                )
        }
)
public class WeekForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WF_ID")
    private Long wfId;

    @Column(name = "REGION_ID")
    private String regionId;

    @Column(name = "MAX_TEMP_3")
    private String maxTemp3;

    @Column(name = "MIN_TEMP_3")
    private String minTemp3;

    @Column(name = "MAX_TEMP_4")
    private String maxTemp4;

    @Column(name = "MIN_TEMP_4")
    private String minTemp4;

    @Column(name = "MAX_TEMP_5")
    private String maxTemp5;

    @Column(name = "MIN_TEMP_5")
    private String minTemp5;

    @Column(name = "MAX_TEMP_6")
    private String maxTemp6;

    @Column(name = "MIN_TEMP_6")
    private String minTemp6;

    @Column(name = "MAX_TEMP_7")
    private String maxTemp7;

    @Column(name = "MIN_TEMP_7")
    private String minTemp7;

    @Column(name = "MAX_TEMP_8")
    private String maxTemp8;

    @Column(name = "MIN_TEMP_8")
    private String minTemp8;

    @Column(name = "MAX_TEMP_9")
    private String maxTemp9;

    @Column(name = "MIN_TEMP_9")
    private String minTemp9;

    @Column(name = "MAX_TEMP_10")
    private String maxTemp10;

    @Column(name = "MIN_TEMP_10")
    private String minTemp10;

    @Column(name = "FORECAST_DATE")
    private LocalDate forecastDate;

    @Column(name = "BASE_TIME")
    private String baseTime;

    public void updateWeekTemp(String baseTime, Item item) {
        this.baseTime = baseTime;
        this.forecastDate = LocalDate.now();
        this.maxTemp3 = item.getTaMax3();
        this.maxTemp4 = item.getTaMax4();
        this.maxTemp5 = item.getTaMax5();
        this.maxTemp6 = item.getTaMax6();
        this.maxTemp7 = item.getTaMax7();
        this.maxTemp8 = item.getTaMax8();
        this.maxTemp9 = item.getTaMax9();
        this.maxTemp10 = item.getTaMax10();
        this.minTemp3 = item.getTaMin3();
        this.minTemp4 = item.getTaMin4();
        this.minTemp5 = item.getTaMin5();
        this.minTemp6 = item.getTaMin6();
        this.minTemp7 = item.getTaMin7();
        this.minTemp8 = item.getTaMin8();
        this.minTemp9 = item.getTaMin9();
        this.minTemp10 = item.getTaMin10();
    }
}
