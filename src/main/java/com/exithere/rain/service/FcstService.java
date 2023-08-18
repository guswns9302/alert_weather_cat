package com.exithere.rain.service;

import com.exithere.rain.dto.response.forecast.*;
import com.exithere.rain.dto.response.forecast.pop.WeekPopForecastResponse;
import com.exithere.rain.dto.response.forecast.shot.Item;
import com.exithere.rain.dto.response.forecast.shot.TwentyFourHoursForecastResponse;
import com.exithere.rain.dto.response.forecast.shot.VilageFcst;
import com.exithere.rain.dto.response.forecast.week.MidTa;
import com.exithere.rain.dto.response.forecast.week.WeekForecastResponse;
import com.exithere.rain.entity.AlarmHistory;
import com.exithere.rain.entity.Region;
import com.exithere.rain.entity.ShortForecast;
import com.exithere.rain.entity.WeekForecast;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.AlarmHistoryRepository;
import com.exithere.rain.repository.ShortForecastRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcstService {

    private final RestUtils<VilageFcst> restUtils;
    private final RegionService regionService;
    private final WeekForecastService weekForecastService;
    private final ShortForecastRepository shortForecastRepository;
    private final WeekPopForecastService weekPopForecastService;
    private final DustForecastService dustForecastService;
    private final AlarmHistoryRepository alarmHistoryRepository;

    @Transactional
    public ForecastResponse reloadFcst(Long regionId, String deviceId) {
        Region getRegion = regionService.findByRegionId(regionId);

        // db에 해당 지역의 예보 정보가 있는지 확인 없으면 기상청 api 호출해서 데이터 저장
        this.requestFcst(getRegion);
        List<ShortForecast> shortForecastList = this.getFcstFromDB(getRegion);

        // 24시간 예보 응답 만들기
        List<TwentyFourHoursForecastResponse> list_24 = new ArrayList<>();
        for(ShortForecast forecast : shortForecastList){
            list_24.add(TwentyFourHoursForecastResponse.from(forecast));
        }

        ForecastResponse forecastResponse = new ForecastResponse();
        forecastResponse.setRegion(getRegion);
        forecastResponse.setCurrentTemp(shortForecastList.get(0).getHourTemp());
        forecastResponse.setHumidity(shortForecastList.get(0).getHumidity());
        forecastResponse.setTwentyFourHoursForecastResponseList(list_24);

        // 주간 날씨 조회
        WeekForecastResponse weekForecastResponse = weekForecastService.weekForecast(getRegion.getRegionName());
        if(weekForecastResponse.getForecastDate() != null){
            // 일 최고 최저 기온 찾기
            LocalDateTime today = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0,0);
            LocalDateTime tomorrow = today.plusDays(3);
            List<ShortForecast> findTodayForecast = shortForecastRepository.findByRegion_RegionIdAndForecastDateTimeBetweenOrderByForecastDateTimeAsc(getRegion.getRegionId(), today, tomorrow);

            for(ShortForecast forecast : findTodayForecast){
                if(forecast.getMaxTemp() != null){
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now())){
                        System.out.println(forecast.getMaxTemp());
                        forecastResponse.setMaxTemp(forecast.getMaxTemp());
                        weekForecastResponse.getZero().setMaxTemp(forecast.getMaxTemp());
                    }
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(1))){
                        weekForecastResponse.getOne().setMaxTemp(forecast.getMaxTemp());
                    }
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(2))){
                        weekForecastResponse.getTwo().setMaxTemp(forecast.getMaxTemp());
                    }
                }

                if(forecast.getMinTemp() != null){
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now())){
                        forecastResponse.setMinTemp(forecast.getMinTemp());
                        weekForecastResponse.getZero().setMinTemp(forecast.getMinTemp());
                    }
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(1))){
                        weekForecastResponse.getOne().setMinTemp(forecast.getMinTemp());
                    }
                    if(forecast.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(2))){
                        weekForecastResponse.getTwo().setMinTemp(forecast.getMinTemp());
                    }
                }
            }

            if(forecastResponse.getMaxTemp() == null){
                forecastResponse.setMaxTemp(weekForecastResponse.getOne().getMaxTemp());
                weekForecastResponse.getZero().setMaxTemp(weekForecastResponse.getOne().getMaxTemp());
            }
            if(forecastResponse.getMinTemp() == null){
                forecastResponse.setMinTemp(weekForecastResponse.getOne().getMinTemp());
                weekForecastResponse.getZero().setMinTemp(weekForecastResponse.getOne().getMinTemp());
            }

            // 주간 강수량 조회
            List<ShortForecast> ofToday = findTodayForecast.stream().filter(i -> i.getForecastDateTime().toLocalDate().equals(LocalDate.now())).collect(Collectors.toList());
            List<ShortForecast> ofOne = findTodayForecast.stream().filter(i -> i.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(1))).collect(Collectors.toList());
            List<ShortForecast> ofTwo = findTodayForecast.stream().filter(i -> i.getForecastDateTime().toLocalDate().equals(LocalDate.now().plusDays(2))).collect(Collectors.toList());

            WeekPopForecastResponse popForWeek = weekPopForecastService.getPopForWeek(getRegion.getRegionName());
            if(popForWeek.getRnSt3() != null){
                weekForecastResponse.getZero().setProbabilityOfPrecipitation(this.getPop(ofToday));
                weekForecastResponse.getOne().setProbabilityOfPrecipitation(this.getPop(ofOne));
                weekForecastResponse.getTwo().setProbabilityOfPrecipitation(this.getPop(ofTwo));
                weekForecastResponse.getThree().setProbabilityOfPrecipitation(popForWeek.getRnSt3());
                weekForecastResponse.getFour().setProbabilityOfPrecipitation(popForWeek.getRnSt4());
                weekForecastResponse.getFive().setProbabilityOfPrecipitation(popForWeek.getRnSt5());
                weekForecastResponse.getSix().setProbabilityOfPrecipitation(popForWeek.getRnSt6());

                weekForecastResponse.getZero().setSkyIcon(String.valueOf(this.getIcon(ofToday)));
                weekForecastResponse.getOne().setSkyIcon(String.valueOf(this.getIcon(ofOne)));
                weekForecastResponse.getTwo().setSkyIcon(String.valueOf(this.getIcon(ofTwo)));
                weekForecastResponse.getThree().setSkyIcon(String.valueOf(popForWeek.getWf3()));
                weekForecastResponse.getFour().setSkyIcon(String.valueOf(popForWeek.getWf4()));
                weekForecastResponse.getFive().setSkyIcon(String.valueOf(popForWeek.getWf5()));
                weekForecastResponse.getSix().setSkyIcon(String.valueOf(popForWeek.getWf6()));

                forecastResponse.setWeekForecastResponse(weekForecastResponse);
            }
        }

        // 미세먼지 추가
        Map<String, String> dustForecast = dustForecastService.getDust(getRegion.getRegionName(), LocalDate.now());
        forecastResponse.setFindDust(dustForecast.get("findDust"));
        forecastResponse.setUltraFineDust(dustForecast.get("ultraFineDust"));

        // 알람 이력 조회
        List<AlarmHistory> alarmHistoryList = alarmHistoryRepository.findByDeviceIdOrderByPushDateTimeDesc(deviceId);
        forecastResponse.setAlarmList(alarmHistoryList);
        return forecastResponse;
    }

    private String getPop(List<ShortForecast> list){
        int pop = 0;
        for(ShortForecast sf : list){
            int listPop = Integer.parseInt(sf.getProbabilityOfPrecipitation());
            pop = (listPop > pop) ? listPop : pop;
        }
        return String.valueOf(pop);
    }

    private int getIcon(List<ShortForecast> list){
        int icon = 0;
        for(ShortForecast sf : list){
            TwentyFourHoursForecastResponse from = TwentyFourHoursForecastResponse.from(sf);
            int listIcon = Integer.parseInt(from.getSkyIcon());
            icon = (listIcon > icon) ? listIcon : icon;
        }
        return icon;
    }

    private List<ShortForecast> getFcstFromDB(Region region){
        LocalDateTime ldt = LocalDateTime.now(); // 현재 시각
        LocalDate ld = ldt.toLocalDate();
        LocalTime lt = LocalTime.of(ldt.getHour(), 0);
        LocalDateTime from = LocalDateTime.of(ld, lt);
        LocalDateTime ldt_after_24 = ldt.plusHours(24);
        LocalDate ld_after_24 = ldt_after_24.toLocalDate();
        LocalTime lt_after_24 = LocalTime.of(ldt_after_24.getHour(), 0);
        LocalDateTime to = LocalDateTime.of(ld_after_24, lt_after_24);

        List<ShortForecast> shortForecastList = shortForecastRepository.findByRegion_RegionIdAndForecastDateTimeBetweenOrderByForecastDateTimeAsc(region.getRegionId(), from, to);

        return shortForecastList;
    }

    private void requestFcst(Region region){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());

        LocalDateTime ldt = LocalDateTime.now(); // 현재 시각
        Map<String, String> base = this.getBaseDateTime(ldt);

        LocalDateTime findTime;
        if(ldt.getHour() + 1 > 23){
            findTime = LocalDateTime.of(ldt.toLocalDate().plusDays(1), LocalTime.of(0, 0));
        }
        else{
            findTime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(ldt.getHour()+1, 0));
        }

        Optional<ShortForecast> existForecast = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), findTime);
        if(existForecast.isPresent()){
            if(existForecast.get().getBaseTime().equals(base.get("baseTime"))){
                return;
            }
        }

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?serviceKey=IlWFCqRejt1YnnGd%2B%2BEL9JGhV7WmdcBIUaXe1Bix02%2FQhdE4Wb%2FG%2BnaORPmfGfD5DpBJJSAX8JxrPqdXnu7Dfg%3D%3D"); /*Service Key*/
        urlBuilder.append("&pageNo=1"); /*페이지번호*/
        urlBuilder.append("&numOfRows=1000"); /*한 페이지 결과 수*/
        urlBuilder.append("&dataType=JSON"); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&base_date=").append(base.get("baseDate")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&base_time=").append(base.get("baseTime")); /*06시 발표(정시단위) */
        urlBuilder.append("&nx=").append(region.getRegionX()); /*예보지점의 X 좌표값*/
        urlBuilder.append("&ny=").append(region.getRegionY()); /*예보지점의 Y 좌표값*/

        ResponseEntity<VilageFcst> response = restUtils.get(URI.create(urlBuilder.toString()), headers, VilageFcst.class);

        if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null){
            throw new CustomException(ErrorCode.OPEN_API_ERROR);
        }
        else{
            VilageFcst vilageFcst = response.getBody();
            if(vilageFcst != null){
                if (!"00".equals(vilageFcst.getResponse().getHeader().resultCode)) {
                    throw new CustomException(ErrorCode.OPEN_API_ERROR);
                } else {
                    List<Item> items = vilageFcst.getResponse().getBody().getItems().getItem();

                    // 한시간 기온
                    List<Item> getTmpList = items.stream().filter(i -> "TMP".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getTmpList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .hourTemp(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updateTMP(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 강수 확률
                    List<Item> getPopList = items.stream().filter(i -> "POP".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getPopList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .probabilityOfPrecipitation(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updatePOP(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 습도
                    List<Item> getRehList = items.stream().filter(i -> "REH".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getRehList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .humidity(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updateREH(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 최고 기온
                    List<Item> getTmxList = items.stream().filter(i -> "TMX".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getTmxList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .maxTemp(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updateTMX(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 최저 기온
                    List<Item> getTmnList = items.stream().filter(i -> "TMN".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getTmnList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .minTemp(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updateTMN(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 하늘 상태
                    List<Item> getSkyList = items.stream().filter(i -> "SKY".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getSkyList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isEmpty()){
                            ShortForecast forecast = ShortForecast.builder()
                                    .region(region)
                                    .skyIcon(item.getFcstValue())
                                    .baseTime(base.get("baseTime"))
                                    .forecastDateTime(forecastDateTime)
                                    .build();

                            shortForecastRepository.save(forecast);
                        }
                        else{
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updateSKY(item.getFcstValue(), base.get("baseTime"));
                        }
                    }

                    // 강수 형태
                    List<Item> getPtyList = items.stream().filter(i -> "PTY".equals(i.getCategory())).collect(Collectors.toList());
                    for(Item item : getPtyList){
                        LocalTime forecastTime = LocalTime.parse(item.getFcstTime(), DateTimeFormatter.ofPattern("HHmm"));
                        LocalDate forecastDate = LocalDate.parse(item.getFcstDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDateTime forecastDateTime = LocalDateTime.of(forecastDate, forecastTime);

                        Optional<ShortForecast> isSfcst = shortForecastRepository.findByRegion_RegionIdAndForecastDateTime(region.getRegionId(), forecastDateTime);
                        if(isSfcst.isPresent()){
                            ShortForecast sfcst = isSfcst.get();
                            sfcst.updatePTY(item.getFcstValue(), base.get("baseTime"));
                        }
                    }
                }
            }
        }
    }

    private Map<String, String> getBaseDateTime(LocalDateTime ldt){
        LocalTime baseTIme_5 = LocalTime.of(5,10);
        LocalTime baseTIme_8 = LocalTime.of(8,10);
        LocalTime baseTIme_11 = LocalTime.of(11,10);
        LocalTime baseTIme_14 = LocalTime.of(14,10);
        LocalTime baseTIme_17 = LocalTime.of(17,10);
        LocalTime baseTIme_20 = LocalTime.of(20,10);
        LocalTime baseTIme_23 = LocalTime.of(23,10);

        String baseDate = ldt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalTime lt = ldt.toLocalTime();
        String baseTime = "";

        if(lt.isBefore(LocalTime.of(3,0))){
            baseDate = ldt.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            baseTime = LocalTime.of(23,0).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(3,0)) || (lt.isAfter(LocalTime.of(3,0)) && lt.isBefore(baseTIme_5))){
            baseTime = LocalTime.of(2,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_5) && lt.isBefore(LocalTime.of(6,0))){
            baseTime = LocalTime.of(2,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(6,0)) || (lt.isAfter(LocalTime.of(6,0)) && lt.isBefore(baseTIme_8))){
            baseTime = LocalTime.of(5,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_8) && lt.isBefore(LocalTime.of(9,0))){
            baseTime = LocalTime.of(5,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(9,0)) || (lt.isAfter(LocalTime.of(9,0)) && lt.isBefore(baseTIme_11))){
            baseTime = LocalTime.of(8,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_11) && lt.isBefore(LocalTime.of(12,0))){
            baseTime = LocalTime.of(8,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(12,0)) || (lt.isAfter(LocalTime.of(12,0)) && lt.isBefore(baseTIme_14))){
            baseTime = LocalTime.of(11,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_14) && lt.isBefore(LocalTime.of(15,0))){
            baseTime = LocalTime.of(11,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(15,0)) || (lt.isAfter(LocalTime.of(15,0)) && lt.isBefore(baseTIme_17))){
            baseTime = LocalTime.of(14,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_17) && lt.isBefore(LocalTime.of(18,0))){
            baseTime = LocalTime.of(14,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.isAfter(LocalTime.of(18,0)) || (lt.isAfter(LocalTime.of(18,0)) && lt.isBefore(baseTIme_20))){
            baseTime = LocalTime.of(17,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_20) && lt.isBefore(LocalTime.of(21,0))){
            baseTime = LocalTime.of(17,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }
        if(lt.equals(LocalTime.of(21,0)) || (lt.isAfter(LocalTime.of(21,0)) && lt.isBefore(baseTIme_23))){
            baseTime = LocalTime.of(20,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        if(lt.isAfter(baseTIme_23)){
            baseTime = LocalTime.of(20,00).format(DateTimeFormatter.ofPattern("HHmm"));
        }

        return Map.of("baseDate", baseDate, "baseTime", baseTime);
    }

    @Transactional
    public void deleteData(){
        LocalDate now = LocalDate.now();
        // 1일 전
        LocalDate now_before4 = now.minusDays(1);

        // 1일 전의 1주 전
        LocalDate now_before4_week = now_before4.minusWeeks(1);

        log.info("OLD FORECAST DATA DELETE -- from : {} | to : {}", now_before4_week, now_before4);

        List<ShortForecast> oldData = shortForecastRepository.findAllByForecastDateTimeBetweenOrderByForecastDateTimeAsc(LocalDateTime.of(now_before4_week, LocalTime.of(0,0,0)), LocalDateTime.of(now_before4, LocalTime.of(23,59,59)));

        shortForecastRepository.deleteAll(oldData);
    }
}
