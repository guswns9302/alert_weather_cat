package com.exithere.rain.service;

import com.exithere.rain.dto.response.forecast.dust.DustFrcst;
import com.exithere.rain.dto.response.forecast.dust.Items;
import com.exithere.rain.entity.DustForecast;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.DustRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DustForecastService {

    private final DustRepository dustRepository;
    private final RestUtils<DustFrcst> restUtils;


    @Transactional
    public Map<String, String> getDust(String regionName, LocalDate ld) {
        String regionDust = "";
        String regionUltraDust = "";

        List<DustForecast> dustList = dustRepository.findByForecastDateOrderByDustIdAsc(ld);

        if(dustList.isEmpty()){
            return Map.of("findDust", "정보 없음", "ultraFineDust", "정보 없음");
        }
        else{
            if(regionName.contains("서울")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getSeoul();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getSeoul();
            }
            else if(regionName.contains("인천")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getIncheon();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getIncheon();
            }
            else if(regionName.contains("제주특별자치도")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getJeju();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getJeju();
            }
            else if(regionName.contains("대전")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getDaejeon();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getDaejeon();
            }
            else if(regionName.contains("세종특별자치시")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getSejong();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getSejong();
            }
            else if(regionName.contains("광주 ")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getGwangju();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getGwangju();
            }
            else if(regionName.contains("대구")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getDaeku();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getDaeku();
            }
            else if(regionName.contains("울산")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getUlsan();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getUlsan();
            }
            else if(regionName.contains("부산")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getBusan();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getBusan();
            }
            else if(regionName.contains("전남")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getJn();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getJn();
            }
            else if(regionName.contains("전북")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getJb();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getJb();
            }
            else if(regionName.contains("경남")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getKn();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getKn();
            }
            else if(regionName.contains("경북")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getKb();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getKb();
            }
            else if(regionName.contains("충남")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getChn();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getChn();
            }
            else if(regionName.contains("충북")){
                regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getChb();
                regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getChb();
            }
            else if(regionName.contains("강원특별자치도")){
                if(regionName.contains("강릉시") || regionName.contains("동해시") || regionName.contains("속초시")
                        || regionName.contains("삼청시") || regionName.contains("태백시") || regionName.contains("고성군")
                        || regionName.contains("양양군")){
                    regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getYd();
                    regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getYd();
                }
                else{ // 영서
                    regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getYs();
                    regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getYs();
                }
            }
            else if(regionName.contains("경기")){
                // 경기 북부
                if(regionName.contains("경기 고양시") || regionName.contains("경기 구리시") || regionName.contains("경기 남양주시")
                        || regionName.contains("경기 동두천시") || regionName.contains("경기 양주시") || regionName.contains("경기 의정부시")
                        || regionName.contains("경기 파주시") || regionName.contains("경기 포천시") || regionName.contains("경기 가평군")
                        || regionName.contains("경기 연천군") || regionName.contains("경기 김포시")){
                    regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getGb();
                    regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getGb();
                }
                else{ // 경기 남부
                    regionDust = dustList.stream().filter(i -> i.getDustType().equals("PM10")).findFirst().get().getGn();
                    regionUltraDust = dustList.stream().filter(i -> i.getDustType().equals("PM25")).findFirst().get().getGn();
                }
            }

            return Map.of("findDust", regionDust, "ultraFineDust", regionUltraDust);
        }
    }

    @Transactional
    public void requestDustForecast(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.toString());

        StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth"); /*URL*/
        urlBuilder.append("?serviceKey=IlWFCqRejt1YnnGd%2B%2BEL9JGhV7WmdcBIUaXe1Bix02%2FQhdE4Wb%2FG%2BnaORPmfGfD5DpBJJSAX8JxrPqdXnu7Dfg%3D%3D"); /*Service Key*/
        urlBuilder.append("&pageNo=1"); /*페이지번호*/
        urlBuilder.append("&numOfRows=100"); /*한 페이지 결과 수*/
        urlBuilder.append("&returnType=json"); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&searchDate=").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        urlBuilder.append("&InformCode=").append("PM10");

        ResponseEntity<DustFrcst> response = restUtils.get(URI.create(urlBuilder.toString()), headers, DustFrcst.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new CustomException(ErrorCode.OPEN_API_ERROR);
        }
        else{
            DustFrcst dustFrcst = response.getBody();
            if(dustFrcst != null){
                if (!"00".equals(dustFrcst.getResponse().getHeader().resultCode)) {
                    //throw new CustomException(ErrorCode.OPEN_API_ERROR);
                    log.error("Dust Error");
                }
                else{
                    List<Items> itemList = dustFrcst.getResponse().getBody().getItems();

                    for(Items item : itemList){
                        if((item.getInformCode().equals("PM10") || item.getInformCode().equals("PM25")) && item.getDataTime().contains("17시 발표")){

                            String[] dustValueList = item.getInformGrade().split(",");

                            Map<String, String> region = new HashMap<>();

                            for(String dustValue : dustValueList){
                                String[] splitValue = dustValue.split(" : ");
                                region.put(splitValue[0], splitValue[1]);
                            }

                            Optional<DustForecast> exist = dustRepository.findByForecastDateAndDustTypeOrderByDustIdAsc(LocalDate.parse(item.getInformData()), item.getInformCode());
                            if(exist.isEmpty()){
                                DustForecast dustForecast = DustForecast.builder()
                                        .dustType(item.getInformCode())
                                        .forecastDate(LocalDate.parse(item.getInformData()))
                                        .baseTime(item.getDataTime())
                                        .seoul(region.get("서울"))
                                        .incheon(region.get("인천"))
                                        .jeju(region.get("제주"))
                                        .daejeon(region.get("대전"))
                                        .sejong(region.get("세종"))
                                        .gwangju(region.get("광주"))
                                        .daeku(region.get("대구"))
                                        .ulsan(region.get("울산"))
                                        .busan(region.get("부산"))
                                        .jn(region.get("전남"))
                                        .jb(region.get("전북"))
                                        .kn(region.get("경남"))
                                        .kb(region.get("경북"))
                                        .chn(region.get("충남"))
                                        .chb(region.get("충북"))
                                        .yd(region.get("영동"))
                                        .ys(region.get("영서"))
                                        .gn(region.get("경기남부"))
                                        .gb(region.get("경기북부"))
                                        .build();

                                dustRepository.save(dustForecast);
                            }
                            else{
                                exist.get().updateDustForecast(item, region);
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void deleteData(LocalDate now_before4, LocalDate now_before4_week){
        List<DustForecast> oldData = dustRepository.findAllByForecastDateBetweenOrderByForecastDateAsc(LocalDateTime.of(now_before4_week, LocalTime.of(0,0,0)), LocalDateTime.of(now_before4, LocalTime.of(23,59,59)));
        dustRepository.deleteAll(oldData);
    }
}
