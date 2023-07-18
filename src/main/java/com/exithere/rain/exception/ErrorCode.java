package com.exithere.rain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터가 유효하지 않습니다."),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    CAN_NOT_ADD_MORE(HttpStatus.BAD_REQUEST, "더 이상 추가할 수 없습니다."),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    CAN_NOT_SELECT_REGION(HttpStatus.BAD_REQUEST, "내 지역 목록에 없는 지역은 선택할 수 없습니다. 관리자에게 문의하세요."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "디바이스 정보를 찾을 수 없습니다."),

    /* 404 NOT_FOUND : REGION 정보를 찾을 수 없음 */
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "지역 설정 정보를 찾을 수 없습니다."),

    /* 404 NOT_FOUND : 주간 POP 예보 정보를 찾을 수 없음 */
    WEEK_POP_FORECAST_NOT_FOUND(HttpStatus.NOT_FOUND, "주간 POP 예보 정보를 찾을 수 없습니다."),

    /* 404 NOT_FOUND : 주간 예보 정보를 찾을 수 없음 */
    WEEK_FORECAST_NOT_FOUND(HttpStatus.NOT_FOUND, "주간 예보 정보를 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌, 중복된 데이터 문제 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌, 중복된 데이터 문제 */
    EXIST_PIN_NOTIFICATION(HttpStatus.CONFLICT, "PIN 공지가 이미 존재합니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
    OPEN_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "기상청 API 호출에 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
