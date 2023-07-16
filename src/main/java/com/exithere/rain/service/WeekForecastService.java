package com.exithere.rain.service;

import com.exithere.rain.dto.response.forecast.week.Item;
import com.exithere.rain.dto.response.forecast.week.MidTa;
import com.exithere.rain.dto.response.forecast.week.RegionIdEnum;
import com.exithere.rain.dto.response.forecast.week.WeekForecastResponse;
import com.exithere.rain.entity.WeekForecast;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.WeekForecastRepository;
import com.exithere.rain.util.RestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeekForecastService {

    private final RestUtils<MidTa> restUtils;
    private final WeekForecastRepository weekForecastRepository;

    @Transactional
    public WeekForecastResponse weekForecast(String regionName){
        String regionCode = RegionIdEnum.find(regionName);

        this.requestWeekForecast(regionCode);

        Optional<WeekForecast> existWeekForecast = weekForecastRepository.findByRegionIdAndForecastDate(regionCode, LocalDate.now());
        return WeekForecastResponse.from(existWeekForecast.get());
    }
    private void requestWeekForecast(String regionCode){
        // 주간 최고, 최저 기온
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());

        String baseTime = this.getBaseTime();

        Optional<WeekForecast> existWfcst = weekForecastRepository.findByRegionIdAndForecastDate(regionCode, LocalDate.now());
        if(existWfcst.isPresent()){
            WeekForecast wfcst = existWfcst.get();
            if(wfcst.getBaseTime().equals(baseTime)){
                return;
            }
        }

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa"); /*URL*/
        urlBuilder.append("?serviceKey=IlWFCqRejt1YnnGd%2B%2BEL9JGhV7WmdcBIUaXe1Bix02%2FQhdE4Wb%2FG%2BnaORPmfGfD5DpBJJSAX8JxrPqdXnu7Dfg%3D%3D"); /*Service Key*/
        urlBuilder.append("&pageNo=1"); /*페이지번호*/
        urlBuilder.append("&numOfRows=10"); /*한 페이지 결과 수*/
        urlBuilder.append("&dataType=JSON"); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&regId=").append(regionCode); /*지역 id*/
        urlBuilder.append("&tmFc=").append(baseTime); /*‘21년 6월 28일 발표*/

        ResponseEntity<MidTa> response = restUtils.get(URI.create(urlBuilder.toString()), headers, MidTa.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new CustomException(ErrorCode.OPEN_API_ERROR);
        } else {
            MidTa midTa = response.getBody();
            if (midTa != null) {
                if (!"00".equals(midTa.getResponse().getHeader().resultCode)) {
                    throw new CustomException(ErrorCode.OPEN_API_ERROR);
                } else {
                    Item item = midTa.getResponse().getBody().getItems().getItem().get(0);

                    Optional<WeekForecast> isWfcst = weekForecastRepository.findByRegionId(regionCode);
                    if(isWfcst.isPresent()){
                        WeekForecast wfcst = isWfcst.get();
                        wfcst.updateWeekTemp(baseTime, item);
                    }
                    else{
                        WeekForecast weekForecast = WeekForecast.builder()
                                .regionId(regionCode)
                                .maxTemp3(item.getTaMax3())
                                .minTemp3(item.getTaMin3())
                                .maxTemp4(item.getTaMax4())
                                .minTemp4(item.getTaMin4())
                                .maxTemp5(item.getTaMax5())
                                .minTemp5(item.getTaMin5())
                                .maxTemp6(item.getTaMax6())
                                .minTemp6(item.getTaMin6())
                                .maxTemp7(item.getTaMax7())
                                .minTemp7(item.getTaMin7())
                                .maxTemp8(item.getTaMax8())
                                .minTemp8(item.getTaMin8())
                                .maxTemp9(item.getTaMax9())
                                .minTemp9(item.getTaMin9())
                                .maxTemp10(item.getTaMax10())
                                .minTemp10(item.getTaMin10())
                                .forecastDate(LocalDate.now())
                                .baseTime(baseTime)
                                .build();
                        weekForecastRepository.save(weekForecast);
                    }
                }
            }
        }
    }

    private String getBaseTime(){
        LocalDateTime ldt = LocalDateTime.now(); // 현재 시각
        LocalTime baseTIme_6 = LocalTime.of(6,00);
        LocalTime baseTIme_18 = LocalTime.of(18,00);

        LocalDate ld = ldt.toLocalDate();
        LocalTime lt = ldt.toLocalTime();
        String baseTime = "";

        // 0시 ~ 6시 -> 전날 18시 기준
        if(lt.equals(LocalTime.of(0,0)) || (lt.isAfter(LocalTime.of(0,0)) && lt.isBefore(LocalTime.of(6,0)))){
            baseTime = ld.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(18,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        // 6시 -> 18시 -> 6시 기준
        if(lt.equals(LocalTime.of(6,0)) || (lt.isAfter(LocalTime.of(6,0)) && lt.isBefore(LocalTime.of(18,0)))){
            baseTime = ld.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(6,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        // 18시 -> 24시 -> 18시 기준
        if(lt.equals(LocalTime.of(18,0)) || (lt.isAfter(LocalTime.of(18,0)) && lt.isBefore(LocalTime.of(23,59,59)))){
            baseTime = ld.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + LocalTime.of(18,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        return baseTime;
    }
}
