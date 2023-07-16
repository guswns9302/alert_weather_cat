package com.exithere.rain.dto.response.forecast;

import com.exithere.rain.dto.response.forecast.shot.TwentyFourHoursForecastResponse;
import com.exithere.rain.dto.response.forecast.week.WeekForecastResponse;
import com.exithere.rain.entity.Region;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ForecastResponse {

    private Region region;
    private String currentTemp;
    private String maxTemp;
    private String minTemp;
    private String humidity;
    private String findDust;
    private String ultraFineDust;
    private List<TwentyFourHoursForecastResponse> twentyFourHoursForecastResponseList;
    private WeekForecastResponse weekForecastResponse;
}
