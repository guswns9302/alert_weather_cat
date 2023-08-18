package com.exithere.rain.service;

import com.exithere.rain.dto.Fcm.TargetDto;
import com.exithere.rain.dto.request.FcmMessageDto;
import com.exithere.rain.dto.response.forecast.ForecastResponse;
import com.exithere.rain.dto.response.forecast.week.Forecast;
import com.exithere.rain.entity.AlarmHistory;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.AlarmHistoryRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/awc-alert-weather-cat/messages:send";
    private final ObjectMapper objectMapper;

    private final DeviceService deviceService;
    private final FcstService fcstService;
    private final DustForecastService dustForecastService;
    private final AlarmHistoryRepository alarmHistoryRepository;

    /*
        알람 종류 - 기상 특보 , 날씨 알림 ( 날씨 요약, 특정 날씨 정보 )
     */
    @Transactional
    public void findDevice() {
        List<TargetDto> allDevice = deviceService.getAllDevice().stream().map(TargetDto::from).collect(Collectors.toList());

        int targetDay = -1;
        if (LocalDate.now().getDayOfWeek().toString().equals("MONDAY")) {
            targetDay = 1;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("TUESDAY")) {
            targetDay = 2;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("WEDNESDAY")) {
            targetDay = 3;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("THURSDAY")) {
            targetDay = 4;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("FRIDAY")) {
            targetDay = 5;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("SATURDAY")) {
            targetDay = 6;
        } else if (LocalDate.now().getDayOfWeek().toString().equals("SUNDAY")) {
            targetDay = 0;
        }

        final int finalTargetDay = targetDay;

        // 기상 특보 on : device
        List<TargetDto> specialReportOnDevice = allDevice.stream().filter(i -> i.isSpecialReport()).collect(Collectors.toList());
        if (!specialReportOnDevice.isEmpty()) {
            //for(TargetDto i : specialReportOnDevice){
            //log.debug("기상 특보 알림(fcm message test)");
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
        if (!pushAlarmOnDevice.isEmpty()) {
            for (TargetDto device : pushAlarmOnDevice) {
                ForecastResponse forecastResponse = fcstService.reloadFcst(device.getSelectRegion().getRegionId(), device.getDeviceId());
                Forecast today = forecastResponse.getWeekForecastResponse().getZero();
                Forecast tomorrow = forecastResponse.getWeekForecastResponse().getOne();
                Map<String, String> tomorrowDust = dustForecastService.getDust(device.getSelectRegion().getRegionName(), LocalDate.now().plusDays(1));
                // 날씨 요약 on
                if (device.getAlarmResponse().isSummery()) {
                    String title = "";
                    String body = "";
                    if (today.getSkyIcon().equals("1")) {
                        // 맑음
                        title += "날씨가 맑다냥!";
                    } else if (today.getSkyIcon().equals("2")) {
                        // 가끔 비
                        title += "때때로 비가 온다냥!";
                    } else if (today.getSkyIcon().equals("3")) {
                        // 가끔 비/눈
                        title += "때대로 눈비가 온다냥!";
                    } else if (today.getSkyIcon().equals("4")) {
                        // 가끔 눈
                        title += "때때로 눈이 온다냥!";
                    } else if (today.getSkyIcon().equals("5")) {
                        // 구름과 해
                        title += "맑지만 구르ㅓㅁ이 많다냥!";
                    } else if (today.getSkyIcon().equals("6")) {
                        // 비
                        title += "비가 온다냥!";
                    } else if (today.getSkyIcon().equals("7")) {
                        // 비/눈
                        title += "눈비가 온다냥!";
                    } else if (today.getSkyIcon().equals("8")) {
                        // 눈
                        title += "눈이 온다냥!";
                    } else if (today.getSkyIcon().equals("9")) {
                        // 흐림
                        title += "날씨가 흐리다냥!";
                    }

                    if (device.getAlarmResponse().getTargetDay() == 0) {
                        // 오늘
                        title = "오늘은 " + title;
                        // 미먼
                        String dustMsg = "";
                        if (forecastResponse.getFindDust().equals(forecastResponse.getUltraFineDust())) {
                            dustMsg = "미세먼지와 초미세먼지는 ";
                            if (forecastResponse.getFindDust().equals("좋음")) {
                                dustMsg += "좋다냥!";
                            } else if (forecastResponse.getFindDust().equals("보통")) {
                                dustMsg += "보통이다냥!";
                            } else if (forecastResponse.getFindDust().equals("나쁨")) {
                                dustMsg += "나쁘다냥!";
                            } else if (forecastResponse.getFindDust().equals("매우나쁨")) {
                                dustMsg += "매우 나쁘다냥!";
                            }
                        }
                        else {
                            dustMsg = "미세먼지는 ";
                            if (forecastResponse.getFindDust().equals("좋음")) {
                                dustMsg += "좋고 ";
                            } else if (forecastResponse.getFindDust().equals("보통")) {
                                dustMsg += "보통이고 ";
                            } else if (forecastResponse.getFindDust().equals("나쁨")) {
                                dustMsg += "나쁘고 ";
                            } else if (forecastResponse.getFindDust().equals("매우나쁨")) {
                                dustMsg += "매우 나쁘고 ";
                            }
                            dustMsg += "초미세먼지는 ";
                            if (forecastResponse.getUltraFineDust().equals("좋음")) {
                                dustMsg += "좋다냥!";
                            } else if (forecastResponse.getUltraFineDust().equals("보통")) {
                                dustMsg += "보통이다냥!";
                            } else if (forecastResponse.getUltraFineDust().equals("나쁨")) {
                                dustMsg += "나쁘다냥!";
                            } else if (forecastResponse.getUltraFineDust().equals("매우나쁨")) {
                                dustMsg += "매우 나쁘다냥!";
                            }
                        }
                        body = "최저 " + today.getMinTemp() + "℃ ~ 최고 " + today.getMaxTemp() + "℃에 강수확률은 " + today.getProbabilityOfPrecipitation() + "%다냥!\r\n" + dustMsg;
                        alarmHistoryRepository.save(AlarmHistory.builder()
                                .deviceId(device.getDeviceId())
                                .regionName(device.getSelectRegion().getRegionName())
                                .pushDateTime(LocalDateTime.now())
                                .title(title)
                                .pushType(0)
                                .temp(today.getMinTemp() + "℃ ~ " + today.getMaxTemp())
                                .pop(Integer.parseInt(today.getProbabilityOfPrecipitation()))
                                .dust(forecastResponse.getFindDust())
                                .ultraDust(forecastResponse.getUltraFineDust())
                                .build()
                        );
                    }
                    else if (device.getAlarmResponse().getTargetDay() == 1) {
                        // 내일
                        title = "내일은 " + title;
                        // 미먼
                        String dustMsg = "";
                        if (tomorrowDust.get("findDust").equals(tomorrowDust.get("ultraFineDust"))) {
                            dustMsg = "미세먼지와 초미세먼지는 ";
                            if (tomorrowDust.get("findDust").equals("좋음")) {
                                dustMsg += "좋다냥!";
                            } else if (tomorrowDust.get("findDust").equals("보통")) {
                                dustMsg += "보통이다냥!";
                            } else if (tomorrowDust.get("findDust").equals("나쁨")) {
                                dustMsg += "나쁘다냥!";
                            } else if (tomorrowDust.get("findDust").equals("매우나쁨")) {
                                dustMsg += "매우 나쁘다냥!";
                            }
                        } else {
                            dustMsg = "미세먼지는 ";
                            if (tomorrowDust.get("findDust").equals("좋음")) {
                                dustMsg += "좋고 ";
                            } else if (tomorrowDust.get("findDust").equals("보통")) {
                                dustMsg += "보통이고 ";
                            } else if (tomorrowDust.get("findDust").equals("나쁨")) {
                                dustMsg += "나쁘고 ";
                            } else if (tomorrowDust.get("findDust").equals("매우나쁨")) {
                                dustMsg += "매우 나쁘고 ";
                            }
                            dustMsg += "초미세먼지는 ";
                            if (tomorrowDust.get("ultraFineDust").equals("좋음")) {
                                dustMsg += "좋다냥!";
                            } else if (tomorrowDust.get("ultraFineDust").equals("보통")) {
                                dustMsg += "보통이다냥!";
                            } else if (tomorrowDust.get("ultraFineDust").equals("나쁨")) {
                                dustMsg += "나쁘다냥!";
                            } else if (tomorrowDust.get("ultraFineDust").equals("매우나쁨")) {
                                dustMsg += "매우 나쁘다냥!";
                            }
                        }

                        body = "최저 " + tomorrow.getMinTemp() + "℃ ~ 최고 " + tomorrow.getMaxTemp() + "℃에 강수확률은 " + tomorrow.getProbabilityOfPrecipitation() + "%다냥!\r\n" + dustMsg;
                        alarmHistoryRepository.save(AlarmHistory.builder()
                                .deviceId(device.getDeviceId())
                                .regionName(device.getSelectRegion().getRegionName())
                                .pushDateTime(LocalDateTime.now())
                                .title(title)
                                .pushType(0)
                                .temp(tomorrow.getMinTemp() + "℃ ~ " + tomorrow.getMaxTemp())
                                .pop(Integer.parseInt(tomorrow.getProbabilityOfPrecipitation()))
                                .dust(tomorrowDust.get("findDust"))
                                .ultraDust(tomorrowDust.get("ultraFineDust"))
                                .build()
                        );
                    }

                    try {
                        this.sendMessage(device.getFcmToken(), title, body);
                    } catch (IOException e) {
                        throw new CustomException(ErrorCode.FCM_PUSH_ERROR);
                    }
                }

                // 특정 날씨
                if (device.getAlarmResponse().isSpecial()) {
                    if (device.getAlarmResponse().isRainFall()) {
                        String title = "";
                        String body = "";
                        String skyStatus = "비 또는 눈이";
                        int pop = 0;
                        // 강수 체크
                        if (device.getAlarmResponse().getTargetDay() == 0) {
                            // 오늘
                            pop = Integer.parseInt(today.getProbabilityOfPrecipitation());

                            String popText = this.popMessage(device.getAlarmResponse().getRatio(), pop);

                            if (popText.length() > 0) {
                                if (today.getSkyIcon().equals("2")) {
                                    skyStatus = "가끔 비가";
                                } else if (today.getSkyIcon().equals("3")) {
                                    skyStatus = "가끔 눈비가";
                                } else if (today.getSkyIcon().equals("4")) {
                                    skyStatus = "가끔 눈이";
                                } else if (today.getSkyIcon().equals("6")) {
                                    skyStatus = "비가";
                                } else if (today.getSkyIcon().equals("7")) {
                                    skyStatus = "눈비가";
                                }  else if (today.getSkyIcon().equals("8")) {
                                    skyStatus = "눈이";
                                }
                                title += "오늘은 " + skyStatus + " 올 수 있다냥";
                                body += popText + " 우산을 챙기라냥!";
                            }
                        } else if (device.getAlarmResponse().getTargetDay() == 1) {
                            // 내일
                            pop = Integer.parseInt(tomorrow.getProbabilityOfPrecipitation());

                            String popText = this.popMessage(device.getAlarmResponse().getRatio(), pop);
                            if (popText.length() > 0) {
                                if (tomorrow.getSkyIcon().equals("2")) {
                                    skyStatus = "가끔 비가";
                                } else if (tomorrow.getSkyIcon().equals("3")) {
                                    skyStatus = "가끔 눈비가";
                                } else if (tomorrow.getSkyIcon().equals("4")) {
                                    skyStatus = "가끔 눈이";
                                } else if (tomorrow.getSkyIcon().equals("6")) {
                                    skyStatus = "비가";
                                } else if (tomorrow.getSkyIcon().equals("7")) {
                                    skyStatus = "눈비가";
                                }  else if (tomorrow.getSkyIcon().equals("8")) {
                                    skyStatus = "눈이";
                                }

                                title += "내일은 " + skyStatus + " 올 수 있다냥";
                                body += popText + " 우산을 챙기라냥!";
                            }
                        }
                        if (title.length() > 0 && body.length() > 0) {
                            alarmHistoryRepository.save(AlarmHistory.builder()
                                    .deviceId(device.getDeviceId())
                                    .regionName(device.getSelectRegion().getRegionName())
                                    .pushDateTime(LocalDateTime.now())
                                    .title(title)
                                    .pushType(2)
                                    .pop(pop)
                                    .build()
                            );

                            try {
                                this.sendMessage(device.getFcmToken(), title, body);
                            } catch (IOException e) {
                                throw new CustomException(ErrorCode.FCM_PUSH_ERROR);
                            }
                        }
                    }
                    if (device.getAlarmResponse().isDust()) {
                        String title = "";
                        String body = "";
                        String dust = "";
                        String ultraDust = "";
                        // 미먼 초미먼 나쁨일때
                        if (device.getAlarmResponse().getTargetDay() == 0) {
                            if (forecastResponse.getFindDust().equals("나쁨") && !forecastResponse.getUltraFineDust().equals("나쁨")) {
                                title += "오늘은 미세먼지가 나쁘다냥!";
                                body += "미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                dust = "나쁨";
                            } else if (forecastResponse.getUltraFineDust().equals("나쁨") && !forecastResponse.getFindDust().equals("나쁨")) {
                                title += "오늘은 초미세먼지가 나쁘다냥!";
                                body += "초미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                ultraDust = "나쁨";
                            } else if (forecastResponse.getFindDust().equals("나쁨") && forecastResponse.getUltraFineDust().equals("나쁨")) {
                                title += "오늘은 미세먼지와 초미세먼지가 나쁘다냥!";
                                body += "미세먼지와 초미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                dust = "나쁨";
                                ultraDust = "나쁨";
                            }
                        } else if (device.getAlarmResponse().getTargetDay() == 1) {
                            if (tomorrowDust.get("findDust").equals("나쁨") && !tomorrowDust.get("ultraFineDust").equals("나쁨")) {
                                title += "내일은 미세먼지가 나쁘다냥!";
                                body += "미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                dust = "나쁨";
                            } else if (tomorrowDust.get("ultraFineDust").equals("나쁨") && !tomorrowDust.get("findDust").equals("나쁨")) {
                                title += "내일은 초미세먼지가 나쁘다냥!";
                                body += "초미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                ultraDust = "나쁨";
                            } else if (tomorrowDust.get("findDust").equals("나쁨") && tomorrowDust.get("ultraFineDust").equals("나쁨")) {
                                title += "내일은 미세먼지와 초미세먼지가 나쁘다냥!";
                                body += "미세먼지와 초미세먼지가 나쁘다냥! 마스크를 챙겨라옹!";
                                dust = "나쁨";
                                ultraDust = "나쁨";
                            }
                        }
                        if (title.length() > 0 && body.length() > 0) {
                            alarmHistoryRepository.save(AlarmHistory.builder()
                                    .deviceId(device.getDeviceId())
                                    .regionName(device.getSelectRegion().getRegionName())
                                    .pushDateTime(LocalDateTime.now())
                                    .title(title)
                                    .pushType(3)
                                    .dust(dust)
                                    .ultraDust(ultraDust)
                                    .build()
                            );
                            try {
                                this.sendMessage(device.getFcmToken(), title, body);
                            } catch (IOException e) {
                                throw new CustomException(ErrorCode.FCM_PUSH_ERROR);
                            }
                        }
                    }
                }
            }
        }
    }

    private String popMessage ( int ratio, int pop){
        String msg = "";
        if (ratio == 0) {
            // 강수확률 40% 이상
            if (40 <= pop) {
                msg = "강수 확률이 40% 이상이다냥!";
            }
        } else if (ratio == 1) {
            // 강수확률 60% 이상
            if (60 <= pop) {
                msg = "강수 확률이 60% 이상이다냥!";
            }
        } else if (ratio == 2) {
            // 강수확률 80% 이상
            if (80 <= pop) {
                msg = "강수 확률이 80% 이상이다냥!";
            }
        }
        return msg;
    }

    // fcm accessToken 할당
    private String getAccessToken () throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    // push message 생성
    private String makeMessage (String targetToken, String title, String body) throws JsonProcessingException {
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
    private void sendMessage (String targetToken, String title, String body) throws IOException {
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
        log.info("******************************************************");
        log.info("push message to token : {}", targetToken);
        log.info("push message success : {}", response.body().string());
        log.info("******************************************************");
    }
}

