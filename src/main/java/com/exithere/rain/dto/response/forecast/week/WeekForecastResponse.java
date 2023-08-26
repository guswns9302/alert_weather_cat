package com.exithere.rain.dto.response.forecast.week;

import com.exithere.rain.entity.WeekForecast;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class WeekForecastResponse {
    private LocalDate forecastDate;
    private Forecast zero;
    private Forecast one;
    private Forecast two;
    private Forecast three;
    private Forecast four;
    private Forecast five;
    private Forecast six;


    public static WeekForecastResponse from(WeekForecast weekForecast) {
        return WeekForecastResponse.builder()
                .forecastDate(weekForecast.getForecastDate())
                .zero(new Forecast())
                .one(new Forecast())
                .two(new Forecast())
                .three(Forecast.from(weekForecast.getMaxTemp3(), weekForecast.getMinTemp3()))
                .four(Forecast.from(weekForecast.getMaxTemp4(), weekForecast.getMinTemp4()))
                .five(Forecast.from(weekForecast.getMaxTemp5(), weekForecast.getMinTemp5()))
                .six(Forecast.from(weekForecast.getMaxTemp6(), weekForecast.getMinTemp6()))
                .build();
    }

//    public static WeekForecastResponse fromAfterOneDay(WeekForecast weekForecast){
//        return WeekForecastResponse.builder()
//                .forecastDate(weekForecast.getForecastDate())
//                .zero(new Forecast())
//                .one(new Forecast())
//                .two(new Forecast())
//                .three(Forecast.from(weekForecast.getMaxTemp4(), weekForecast.getMinTemp4()))
//                .four(Forecast.from(weekForecast.getMaxTemp5(), weekForecast.getMinTemp5()))
//                .five(Forecast.from(weekForecast.getMaxTemp6(), weekForecast.getMinTemp6()))
//                .six(Forecast.from(weekForecast.getMaxTemp7(), weekForecast.getMinTemp7()))
//                .build();
//    }
}
