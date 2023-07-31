package com.exithere.rain.service;

import com.exithere.rain.dto.response.forecast.pop.Item;
import com.exithere.rain.dto.response.forecast.pop.MidLand;
import com.exithere.rain.dto.response.forecast.pop.PopRegionIdEnum;
import com.exithere.rain.dto.response.forecast.pop.WeekPopForecastResponse;
import com.exithere.rain.entity.WeekPopForecast;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.WeekPopForecastRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeekPopForecastService {

    private final WeekPopForecastRepository weekPopForecastRepository;
    private final RestUtils<MidLand> restUtils;


    @Transactional
    public WeekPopForecastResponse getPopForWeek(String regionName) {
        String regionCode = PopRegionIdEnum.find(regionName);
        log.info("주간 강수 - region name : {} / region code : {}", regionName, regionCode);
        Optional<WeekPopForecast> exist = weekPopForecastRepository.findByRegionIdAndForecastDate(regionCode, LocalDate.now());
        if(exist.isEmpty()){
            //throw new CustomException(ErrorCode.WEEK_POP_FORECAST_NOT_FOUND);
            return WeekPopForecastResponse.builder().build();
        }
        else{
            return WeekPopForecastResponse.from(exist.get());
        }
    }

    @Transactional
    public void requestWeekPopForecast(String tmFc){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());

        List<WeekPopForecast> weekPopForecastList = new ArrayList<>();

        for(String regionCode : PopRegionIdEnum.get()){
            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst"); /*URL*/
            urlBuilder.append("?serviceKey=IlWFCqRejt1YnnGd%2B%2BEL9JGhV7WmdcBIUaXe1Bix02%2FQhdE4Wb%2FG%2BnaORPmfGfD5DpBJJSAX8JxrPqdXnu7Dfg%3D%3D"); /*Service Key*/
            urlBuilder.append("&pageNo=1"); /*페이지번호*/
            urlBuilder.append("&numOfRows=10"); /*한 페이지 결과 수*/
            urlBuilder.append("&dataType=JSON"); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&regId=").append(regionCode); /*지역 id*/
            urlBuilder.append("&tmFc=").append(tmFc); /*‘21년 6월 28일 발표*/
            ResponseEntity<MidLand> response = restUtils.get(URI.create(urlBuilder.toString()), headers, MidLand.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CustomException(ErrorCode.OPEN_API_ERROR);
            }
            else{
                MidLand midLand = response.getBody();
                if(midLand != null){
                    if (!"00".equals(midLand.getResponse().getHeader().resultCode)) {
                        throw new CustomException(ErrorCode.OPEN_API_ERROR);
                    }
                    else{
                        Item item = midLand.getResponse().getBody().getItems().getItem().get(0);

                        Optional<WeekPopForecast> isWpfcst = weekPopForecastRepository.findByRegionId(regionCode);
                        if(isWpfcst.isPresent()){
                            WeekPopForecast wpfcst = isWpfcst.get();
                            wpfcst.updateWeekPop(tmFc, item);
                        }
                        else{
                            WeekPopForecast weekPopForecast = WeekPopForecast.builder()
                                    .regionId(regionCode)
                                    .baseTime(tmFc)
                                    .forecastDate(LocalDate.now())
                                    .wf3Am(item.getWf3Am())
                                    .wf4Am(item.getWf4Am())
                                    .wf5Am(item.getWf5Am())
                                    .wf6Am(item.getWf6Am())
                                    .wf7Am(item.getWf7Am())
                                    .wf8(item.getWf8())
                                    .wf9(item.getWf9())
                                    .wf10(item.getWf10())
                                    .wf3Pm(item.getWf3Pm())
                                    .wf4Pm(item.getWf4Pm())
                                    .wf5Pm(item.getWf5Pm())
                                    .wf6Pm(item.getWf6Pm())
                                    .wf7Pm(item.getWf7Pm())
                                    .rnSt3Am(item.getRnSt3Am())
                                    .rnSt4Am(item.getRnSt4Am())
                                    .rnSt5Am(item.getRnSt5Am())
                                    .rnSt6Am(item.getRnSt6Am())
                                    .rnSt7Am(item.getRnSt7Am())
                                    .rnSt8(item.getRnSt8())
                                    .rnSt9(item.getRnSt9())
                                    .rnSt10(item.getRnSt10())
                                    .rnSt3Pm(item.getRnSt3Pm())
                                    .rnSt4Pm(item.getRnSt4Pm())
                                    .rnSt5Pm(item.getRnSt5Pm())
                                    .rnSt6Pm(item.getRnSt6Pm())
                                    .rnSt7Pm(item.getRnSt7Pm())
                                    .build();

                            weekPopForecastList.add(weekPopForecast);
                        }
                    }
                }
            }
        }

        if(!weekPopForecastList.isEmpty()){
            weekPopForecastRepository.saveAll(weekPopForecastList);
        }
    }

}
