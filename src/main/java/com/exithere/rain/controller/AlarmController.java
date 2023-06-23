package com.exithere.rain.controller;

import com.exithere.rain.dto.request.AlarmRequest;
import com.exithere.rain.dto.request.DeviceJoinRequest;
import com.exithere.rain.dto.response.AlarmResponse;
import com.exithere.rain.dto.response.DeviceTotalResponse;
import com.exithere.rain.exception.ErrorResponse;
import com.exithere.rain.service.AlarmService;
import com.exithere.rain.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Device", description = "디바이스별 설정과 관련된 API")
@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "update push alarm setting", description = "푸시 알람 세부 설정을 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AlarmResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/push/setting")
    public ResponseEntity<AlarmResponse> pushSettingUpdate(@RequestBody AlarmRequest alarmRequest){
        return ResponseEntity.ok(alarmService.updateSetting(alarmRequest));
    }
}
