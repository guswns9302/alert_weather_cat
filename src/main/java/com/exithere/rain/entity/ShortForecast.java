package com.exithere.rain.entity;

import lombok.*;

import javax.naming.Name;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="TB_SHORT_FORECAST",
    uniqueConstraints={
            @UniqueConstraint(
                    columnNames={"REGION_ID", "FORECAST_DATE_TIME"}
            )
    }
)
public class ShortForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SF_ID")
    private Long sfId;

    @ManyToOne
    @JoinColumn(name = "REGION_ID")
    private Region region;

    @Column(name = "MAX_TEMP")
    private String maxTemp;

    @Column(name = "MIN_TEMP")
    private String minTemp;

    @Column(name = "HOUR_TEMP")
    private String hourTemp;

    @Column(name = "PROBABILITY_OF_PRECIPITATION")
    private String probabilityOfPrecipitation;

    @Column(name = "HUMIDITY")
    private String humidity;

    @Column(name = "SKY_ICON")
    private String skyIcon;

    @Column(name = "FORECAST_DATE_TIME")
    private LocalDateTime forecastDateTime;

    @Column(name = "BASE_TIME")
    private String baseTime;

    public void updateTMP(String tmp, String baseTime){
        this.hourTemp = tmp;
        this.baseTime = baseTime;
    }

    public void updatePOP(String pop, String baseTime){
        this.probabilityOfPrecipitation = pop;
        this.baseTime = baseTime;
    }

    public void updateREH(String reh, String baseTime){
        this.humidity = reh;
        this.baseTime = baseTime;
    }

    public void updateTMX(String tmx, String baseTime){
        this.maxTemp = tmx;
        this.baseTime = baseTime;
    }

    public void updateTMN(String tmn, String baseTime){
        this.minTemp = tmn;
        this.baseTime = baseTime;
    }

    public void updateSKY(String sky, String baseTime){
        this.skyIcon = sky;
        this.baseTime = baseTime;
    }

    public void updatePTY(String pty, String baseTime){
        this.skyIcon += pty;
        this.baseTime = baseTime;
    }
}
