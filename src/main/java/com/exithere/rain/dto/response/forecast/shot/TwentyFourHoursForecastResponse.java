package com.exithere.rain.dto.response.forecast.shot;

import com.exithere.rain.entity.ShortForecast;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TwentyFourHoursForecastResponse {
    private String temp;
    private String probabilityOfPrecipitation;
    private String skyIcon;
    private LocalDateTime forecastTime;

    public static TwentyFourHoursForecastResponse from(ShortForecast shortForecast){
        String skyIcon = "";
        if(shortForecast.getSkyIcon().equals("10")){
            skyIcon = "1";
        }

        if(shortForecast.getSkyIcon().equals("11") || shortForecast.getSkyIcon().equals("12") || shortForecast.getSkyIcon().equals("13") || shortForecast.getSkyIcon().equals("14")){
            skyIcon = "4";
        }

        if(shortForecast.getSkyIcon().equals("30")){
            skyIcon = "2";
        }

        if(shortForecast.getSkyIcon().equals("31") || shortForecast.getSkyIcon().equals("34") || shortForecast.getSkyIcon().equals("41") || shortForecast.getSkyIcon().equals("44")){
            skyIcon = "6";
        }

        if(shortForecast.getSkyIcon().equals("32") || shortForecast.getSkyIcon().equals("42")){
            skyIcon = "5";
        }

        if(shortForecast.getSkyIcon().equals("33") || shortForecast.getSkyIcon().equals("43")){
            skyIcon = "7";
        }

        if(shortForecast.getSkyIcon().equals("40")){
            skyIcon = "3";
        }

        return TwentyFourHoursForecastResponse.builder()
                .temp(shortForecast.getHourTemp())
                .probabilityOfPrecipitation(shortForecast.getProbabilityOfPrecipitation())
                .skyIcon(skyIcon)
                .forecastTime(shortForecast.getForecastDateTime())
                .build();
    }
}
