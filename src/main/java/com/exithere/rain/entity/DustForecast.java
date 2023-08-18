package com.exithere.rain.entity;

import com.exithere.rain.dto.response.forecast.dust.Items;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="TB_DUST_FORECAST")
public class DustForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DUST_ID")
    private Long dustId;

    @Column(name = "DUST_TYPE")
    private String dustType;

    @Column(name = "FORECAST_DATE")
    private LocalDate forecastDate;

    @Column(name = "BASE_TIME")
    private String baseTime;

    @Column(name = "SEOUL")
    private String seoul;

    @Column(name = "INCHEON")
    private String incheon;

    @Column(name = "JEJU")
    private String jeju;

    @Column(name = "DAEJEON")
    private String daejeon;

    @Column(name = "SEJONG")
    private String sejong;

    @Column(name = "GWANGJU")
    private String gwangju;

    @Column(name = "DAEKU")
    private String daeku;

    @Column(name = "ULSAN")
    private String ulsan;

    @Column(name = "BUSAN")
    private String busan;

    @Column(name = "JN")
    private String jn;

    @Column(name = "JB")
    private String jb;

    @Column(name = "KN")
    private String kn;

    @Column(name = "KB")
    private String kb;

    @Column(name = "CHN")
    private String chn;

    @Column(name = "CHB")
    private String chb;

    @Column(name = "YD")
    private String yd;

    @Column(name = "YS")
    private String ys;

    @Column(name = "GN")
    private String gn;

    @Column(name = "GB")
    private String gb;

    public void updateDustForecast(Items item, Map<String, String> region) {
        this.baseTime = item.getDataTime();
        this.forecastDate = LocalDate.parse(item.getInformData());
        this.seoul = region.get("서울");
        this.incheon = region.get("인천");
        this.jeju = region.get("제주");
        this.daejeon = region.get("대전");
        this.sejong = region.get("세종");
        this.gwangju = region.get("광주");
        this.daeku = region.get("대구");
        this.ulsan = region.get("울산");
        this.busan = region.get("부산");
        this.jn = region.get("전남");
        this.jb = region.get("전북");
        this.kn = region.get("경남");
        this.kb = region.get("경북");
        this.chn = region.get("충남");
        this.chb = region.get("충북");
        this.yd = region.get("영동");
        this.ys = region.get("영서");
        this.gn = region.get("경기남부");
        this.gb = region.get("경기북부");
    }
}
