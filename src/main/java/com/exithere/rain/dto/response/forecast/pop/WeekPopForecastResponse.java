package com.exithere.rain.dto.response.forecast.pop;

import com.exithere.rain.dto.response.forecast.week.Forecast;
import com.exithere.rain.entity.WeekForecast;
import com.exithere.rain.entity.WeekPopForecast;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class WeekPopForecastResponse {
    private String rnSt3;
    private String rnSt4;
    private String rnSt5;
    private String rnSt6;
    private String rnSt7;
    private int wf3;
    private int wf4;
    private int wf5;
    private int wf6;
    private int wf7;

    public static WeekPopForecastResponse from(WeekPopForecast weekPopForecast) {
        int wf3am = getSkyCode(weekPopForecast.getWf3Am());
        int wf4am = getSkyCode(weekPopForecast.getWf4Am());
        int wf5am = getSkyCode(weekPopForecast.getWf5Am());
        int wf6am = getSkyCode(weekPopForecast.getWf6Am());
        int wf7am = getSkyCode(weekPopForecast.getWf7Am());
        int wf3pm = getSkyCode(weekPopForecast.getWf3Pm());
        int wf4pm = getSkyCode(weekPopForecast.getWf4Pm());
        int wf5pm = getSkyCode(weekPopForecast.getWf5Pm());
        int wf6pm = getSkyCode(weekPopForecast.getWf6Pm());
        int wf7pm = getSkyCode(weekPopForecast.getWf7Pm());

        return WeekPopForecastResponse.builder()
                .rnSt3((Integer.parseInt(weekPopForecast.getRnSt3Am()) > Integer.parseInt(weekPopForecast.getRnSt3Pm())) ? weekPopForecast.getRnSt3Am() : weekPopForecast.getRnSt3Pm())
                .rnSt4((Integer.parseInt(weekPopForecast.getRnSt4Am()) > Integer.parseInt(weekPopForecast.getRnSt4Pm())) ? weekPopForecast.getRnSt4Am() : weekPopForecast.getRnSt4Pm())
                .rnSt5((Integer.parseInt(weekPopForecast.getRnSt5Am()) > Integer.parseInt(weekPopForecast.getRnSt5Pm())) ? weekPopForecast.getRnSt5Am() : weekPopForecast.getRnSt5Pm())
                .rnSt6((Integer.parseInt(weekPopForecast.getRnSt6Am()) > Integer.parseInt(weekPopForecast.getRnSt6Pm())) ? weekPopForecast.getRnSt6Am() : weekPopForecast.getRnSt6Pm())
                .rnSt7((Integer.parseInt(weekPopForecast.getRnSt7Am()) > Integer.parseInt(weekPopForecast.getRnSt7Pm())) ? weekPopForecast.getRnSt7Am() : weekPopForecast.getRnSt7Pm())
                .wf3((wf3am > wf3pm) ? wf3am : wf3pm)
                .wf4((wf4am > wf4pm) ? wf4am : wf4pm)
                .wf5((wf5am > wf5pm) ? wf5am : wf5pm)
                .wf6((wf6am > wf6pm) ? wf6am : wf6pm)
                .wf7((wf7am > wf7pm) ? wf7am : wf7pm)
                .build();
    }

    private static int getSkyCode(String value) {
        int wf3am = 1;
        if(value.equals("맑음")){
            wf3am = 1;
        }
        else if(value.equals("구름많음")){
            wf3am = 2;
        }
        else if(value.equals("구름많고 비") || value.equals("구름많고 소나기") || value.equals("흐리고 비") || value.equals("흐리고 소나기")){
            wf3am = 6;
        }
        else if(value.equals("구름많고 비/눈") || value.equals("흐리고 비/눈")){
            wf3am = 5;
        }
        else if(value.equals("구름많고 눈") || value.equals("흐리고 눈")){
            wf3am = 7;
        }
        else if(value.equals("흐림")){
            wf3am = 3;
        }
        return wf3am;
    }

}
