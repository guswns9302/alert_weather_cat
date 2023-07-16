package com.exithere.rain.dto.response.forecast.shot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    // 발표 일자
    private String baseDate;
    // 발표 시각
    private String baseTime;
    // 자료 구분
    private String category;
    // 예보 일자
    private String fcstDate;
    // 예보 시각
    private String fcstTime;
    // 예보 값
    private String fcstValue;
    // 예보 지점 x좌표
    private int nx;
    // 예보 지점 y좌표
    private int ny;
}
