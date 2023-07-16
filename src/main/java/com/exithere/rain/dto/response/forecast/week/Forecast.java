package com.exithere.rain.dto.response.forecast.week;

import com.exithere.rain.entity.WeekForecast;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {
    private String maxTemp;
    private String minTemp;
    private String probabilityOfPrecipitation;
    private String skyIcon;

    public static Forecast from(String maxTemp, String minTemp){
        return Forecast.builder()
                .maxTemp(maxTemp)
                .minTemp(minTemp)
                .build();
    }
}
