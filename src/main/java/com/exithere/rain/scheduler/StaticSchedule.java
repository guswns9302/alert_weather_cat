package com.exithere.rain.scheduler;

import com.exithere.rain.service.DustForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaticSchedule {

    private final DustForecastService dustForecastService;

    // 미먼(pm10) 초미먼(pm25) 주간 예보 - 매일 1회, 17시 30분 19개 권역
    @Async
    @Scheduled(cron = "0 30 17 * * *")
    public void dustP(){
        dustForecastService.requestDustForecast();
    }




}
