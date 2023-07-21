package com.exithere.rain.service;

import com.exithere.rain.dto.Fcm.TargetDto;
import com.exithere.rain.dto.request.FcmMessageDto;
import com.exithere.rain.dto.response.AlarmResponse;
import com.exithere.rain.dto.response.forecast.ForecastResponse;
import com.exithere.rain.dto.response.forecast.week.Forecast;
import com.exithere.rain.entity.Device;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/awc-alert-weather-cat/messages:send";
    private final ObjectMapper objectMapper;

    private final DeviceService deviceService;
    private final FcstService fcstService;

    /*
        알람 종류 - 기상 특보 , 날씨 알림 ( 날씨 요약, 특정 날씨 정보 )
     */
    @Transactional
    public void findDevice(){
        List<TargetDto> allDevice = deviceService.getAllDevice().stream().map(TargetDto::from).collect(Collectors.toList());

        int targetDay = -1;
        if(LocalDate.now().getDayOfWeek().toString().equals("MONDAY")){
            targetDay = 1;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("TUESDAY")){
            targetDay = 2;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("WEDNESDAY")){
            targetDay = 3;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("THURSDAY")){
            targetDay = 4;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("FRIDAY")){
            targetDay = 5;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("SATURDAY")){
            targetDay = 6;
        }
        else if(LocalDate.now().getDayOfWeek().toString().equals("TUESDAY")){
            targetDay = 0;
        }

        final int finalTargetDay = targetDay;

        // 기상 특보 on : device
        List<TargetDto> specialReportOnDevice = allDevice.stream().filter(i -> i.isSpecialReport()).collect(Collectors.toList());
        if(!specialReportOnDevice.isEmpty()){
            //for(TargetDto i : specialReportOnDevice){
                log.debug("기상 특보 알림(fcm message test)");
//                try {
//                    this.sendMessage("eoI5TqTMRnigLwwM-Zd366:APA91bGPa4iLu9ole2V0h1A8mIc7-2kTNE5Xh5sQEsyCjfV0x752PH0Tcpo3kCMDq9lZF2FvGg-tixha-cKBJ7gnEXEol-pMSb6Dch07WLVqkh7JAwfyJq1jCJ2S-ozYlvAjHCrUM0ZO", "title", "body");
//                } catch (IOException e) {
//                    log.error("fcm message push fail : {}", e);
//                }
            //}
        }

        // 날씨 알림 on
        List<TargetDto> pushAlarmOnDevice = allDevice.stream()
                .filter(i -> i.isPushBtn())
                .filter(i -> i.getAlarmResponse().getTargetDate().contains(finalTargetDay))
                .filter(i -> i.getAlarmResponse().getTargetTime().equals(LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())))
                .collect(Collectors.toList());
        if(!pushAlarmOnDevice.isEmpty()){
            for(TargetDto device : pushAlarmOnDevice){
                ForecastResponse forecastResponse = fcstService.reloadFcst(device.getSelectRegion().getRegionName(), device.getSelectRegion().getRegionX(), device.getSelectRegion().getRegionY());
                Forecast today = forecastResponse.getWeekForecastResponse().getZero();
                Forecast tomorrow = forecastResponse.getWeekForecastResponse().getOne();
                // 날씨 요약 on
                if(device.getAlarmResponse().isSummery()){
                    if(device.getAlarmResponse().getTargetDay() == 0){
                        // 오늘
                        log.debug("오늘 날씨는 {} // 온도 : {} ~ {} // 강수 확률 : {}", today.getSkyIcon(), today.getMinTemp(), today.getMaxTemp(), today.getProbabilityOfPrecipitation());
                    }
                    else if(device.getAlarmResponse().getTargetDay() == 1){
                        // 내일
                        log.debug("내일 날씨는 {} // 온도 : {} ~ {} // 강수 확률 : {}", tomorrow.getSkyIcon(), tomorrow.getMinTemp(), tomorrow.getMaxTemp(), tomorrow.getProbabilityOfPrecipitation());
                    }

                }

                if(device.getAlarmResponse().isSpecial()){
                    if(device.getAlarmResponse().isRainFall()){
                        if(device.getAlarmResponse().getTargetDay() == 0){
                            // 오늘
                            int pop = Integer.parseInt(today.getProbabilityOfPrecipitation());

                            String popText = this.popMessage(device.getAlarmResponse().getRatio(), pop);
                            if(popText.length() > 0){
                                log.debug("pop 푸쉬 알람 : {}", "오늘 " + popText);
                            }
                        }
                        else if(device.getAlarmResponse().getTargetDay() == 1){
                            // 내일
                            int pop = Integer.parseInt(tomorrow.getProbabilityOfPrecipitation());

                            String popText = this.popMessage(device.getAlarmResponse().getRatio(), pop);
                            if(popText.length() > 0){
                                log.debug("pop 푸쉬 알람 : {}", "내일 " + popText);
                            }
                        }
                    }
                    else if(device.getAlarmResponse().isDust()){
                        // 미먼 초미먼 나쁨일때
                        if(forecastResponse.getFindDust().equals("나쁨")){
                            log.debug("미세먼지 푸쉬 알람 - 나쁨");
                        }

                        if(forecastResponse.getUltraFineDust().equals("나쁨")){
                            log.debug("초 미세먼지 푸쉬 알람 - 나쁨");
                        }
                    }
                }
            }
        }
    }

    private String popMessage(int ratio, int pop){
        String msg = "";
        if(ratio == 0){
            // 강수확률 40% 이상
            if(40 <= pop){
                msg = "강수량이 40% 이상입니다.";
            }
        }
        else if(ratio == 1){
            // 강수확률 60% 이상
            if(60 <= pop){
                msg = "강수량이 60% 이상입니다.";
            }
        }
        else if(ratio == 2){
            // 강수확률 80% 이상
            if(80 <= pop){
                msg = "강수량이 80% 이상입니다.";
            }
        }

        return msg;
    }

    // fcm accessToken 할당
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                                                                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    // push message 생성
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                                                .token(targetToken)
                                                .notification(FcmMessageDto.Notification.builder()
                                                                                        .title(title)
                                                                                        .body(body)
                                                                                        .build())
                                                .build())
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }

    // fcm message 전송
    private void sendMessage(String targetToken, String title, String body) throws IOException {
        String message = this.makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        log.info("push message success : {}", response.body().string());
    }
}
