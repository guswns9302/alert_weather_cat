package com.exithere.rain.scheduler;

import com.exithere.rain.dto.response.forecast.pop.PopRegionIdEnum;
import com.exithere.rain.dto.response.forecast.week.RegionIdEnum;
import com.exithere.rain.entity.WeekPopForecast;
import com.exithere.rain.service.DustForecastService;
import com.exithere.rain.service.WeekForecastService;
import com.exithere.rain.service.WeekPopForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaticSchedule {

    private final DustForecastService dustForecastService;
    private final WeekForecastService weekForecastService;
    private final WeekPopForecastService weekPopForecastService;

    // 미먼(pm10) 초미먼(pm25) 주간 예보 - 매일 1회, 17시 30분 19개 권역
    @Async
    @Scheduled(cron = "0 30 17 * * *")
    public void dustP(){
        dustForecastService.requestDustForecast();
    }

    // 06시 18시 -> 주간 최고 최저 기온
    @Async
    @Scheduled(cron = "0 10 6 * * *")
    public void week06(){
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(6,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);

    }

    @Async
    @Scheduled(cron = "0 0 11 * * *")
    public void week11(){
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(6,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);

    }

    @Async
    @Scheduled(cron = "0 10 18 * * *")
    public void week18(){
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(18,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);

    }

    // 6시 18시 -> 주간 강수 및 기상 상태
    @Async
    @Scheduled(cron = "0 10 6 * * *")
    public void weekPop06(){
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(6,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
    }

    @Async
    @Scheduled(cron = "0 0 11 * * *")
    public void weekPop11(){
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(6,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
    }

    @Async
    @Scheduled(cron = "0 10 18 * * *")
    public void weekPop18(){
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(18,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
    }


}
