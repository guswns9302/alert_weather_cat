package com.exithere.rain.scheduler;

import com.exithere.rain.dto.response.forecast.pop.PopRegionIdEnum;
import com.exithere.rain.dto.response.forecast.week.RegionIdEnum;
import com.exithere.rain.entity.WeekPopForecast;
import com.exithere.rain.service.*;
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

    private final FcstService fcstService;
    private final DustForecastService dustForecastService;
    private final WeekForecastService weekForecastService;
    private final WeekPopForecastService weekPopForecastService;
    private final FcmMessageService fcmMessageService;

    // 미먼(pm10) 초미먼(pm25) 주간 예보 - 매일 1회, 17시 30분 19개 권역
    @Async
    @Scheduled(cron = "0 30 17 * * *")
    public void dustP(){
        log.info("미세먼지 정보 조회 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dustForecastService.requestDustForecast();
        log.info("==== 미세먼지 정보 조회 종료 ====");
    }

    // 06시 18시 -> 주간 최고 최저 기온
    @Async
    @Scheduled(cron = "0 10 6 * * *")
    public void week06(){
        log.info("주간 최고 최저 기온 정보 조회 06시 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(6,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);
        log.info("==== 주간 최고 최저 기온 06시 정보 조회 종료 ====");
    }

    @Async
    @Scheduled(cron = "0 10 18 * * *")
    public void week18(){
        log.info("주간 최고 최저 기온 정보 조회 18시 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(18,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);
        log.info("==== 주간 최고 최저 기온 18시 정보 조회 종료 ====");
    }

    // 6시 18시 -> 주간 강수 및 기상 상태
    @Async
    @Scheduled(cron = "0 10 6 * * *")
    public void weekPop06(){
        log.info("주간 강수 정보 조회 06시 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(6,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
        log.info("==== 주간 강수 06시 정보 조회 종료 ====");
    }

    @Async
    @Scheduled(cron = "0 10 18 * * *")
    public void weekPop18(){
        log.info("주간 강수 정보 조회 18시 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(18,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
        log.info("==== 주간 강수 18시 정보 조회 종료 ====");
    }

    // push
    @Async
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendAlarm(){
        fcmMessageService.findDevice();
    }

    // old data delete
    @Async
    @Scheduled(cron = "0 59 23 * * SUN")
    public void deleteDB(){
        log.info("오래된 정보 삭제 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fcstService.deleteData();
    }

    // 로컬 테스트
    @Async
    @Scheduled(cron = "0 2 10 * * *")
    public void week11(){
        String baseTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(6,00).format(DateTimeFormatter.ofPattern("HHmm"));
        weekForecastService.requestWeekForecast(baseTime);

    }

    @Async
    @Scheduled(cron = "0 2 10 * * *")
    public void weekPop11(){
        String baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(6,0,0)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        weekPopForecastService.requestWeekPopForecast(baseTime);
    }

    @Async
    @Scheduled(cron = "0 5 11 * * *")
    public void dustP11(){
        log.info("미세먼지 정보 조회 - {}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dustForecastService.requestDustForecast();
        log.info("==== 미세먼지 정보 조회 종료 ====");
    }

}
