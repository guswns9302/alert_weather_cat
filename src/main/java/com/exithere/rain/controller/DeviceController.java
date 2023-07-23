package com.exithere.rain.controller;

import com.exithere.rain.dto.request.DeviceJoinRequest;
import com.exithere.rain.dto.request.DeviceRegionRequest;
import com.exithere.rain.dto.request.DeviceRegionSelectOrDeleteRequest;
import com.exithere.rain.dto.response.AlarmResponse;
import com.exithere.rain.dto.response.DeviceTotalResponse;
import com.exithere.rain.dto.response.RegionListResponse;
import com.exithere.rain.exception.ErrorResponse;
import com.exithere.rain.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Device", description = "디바이스별 설정과 관련된 API")
@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "get device all setting", description = "스플래쉬 화면, 즉 앱 첫 구동 시 실행될 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = DeviceTotalResponse.class)))
    })
    @PostMapping ("/join")
    public ResponseEntity<DeviceTotalResponse> join(@RequestBody DeviceJoinRequest deviceJoinRequest){
        return ResponseEntity.ok(deviceService.loadAndSaveUser(deviceJoinRequest));
    }

    @Operation(summary = "toggle push button", description = "푸시 알림 버튼 상태 변경 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameters({
            @Parameter(name = "deviceId", description = "디바이스 Id", example = "", in = ParameterIn.PATH)
    })
    @PutMapping("/push/{deviceId}")
    public ResponseEntity<Map<String, Boolean>> pushBTNUpdate(@PathVariable String deviceId){
        return ResponseEntity.ok(deviceService.togglePushBtn(deviceId));
    }

    @Operation(summary = "toggle special report button", description = "기상 특보 알림 버튼 상태 변경 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameters({
            @Parameter(name = "deviceId", description = "디바이스 Id", example = "", in = ParameterIn.PATH)
    })
    @PutMapping("/push/special/{deviceId}")
    public ResponseEntity<Map<String, Boolean>> specialReportBTNUpdate(@PathVariable String deviceId){
        return ResponseEntity.ok(deviceService.toggleSpecialBtn(deviceId));
    }

    @Operation(summary = "add region", description = "지역 추가 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RegionListResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/region")
    public ResponseEntity<RegionListResponse> addRegion(@RequestBody DeviceRegionRequest deviceRegionRequest){
        return ResponseEntity.ok(deviceService.updateRegion(deviceRegionRequest));
    }

    @Operation(summary = "select region update", description = "지역 선택 변경 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RegionListResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/region")
    public ResponseEntity<RegionListResponse> selectRegion(@RequestBody DeviceRegionSelectOrDeleteRequest deviceRegionSelectOrDeleteRequest){
        return ResponseEntity.ok(deviceService.updateSelectRegion(deviceRegionSelectOrDeleteRequest));
    }

    @DeleteMapping("/region")
    public ResponseEntity<RegionListResponse> deleteRegion(@RequestBody DeviceRegionSelectOrDeleteRequest deviceRegionSelectOrDeleteRequest){
        return ResponseEntity.ok(deviceService.deleteRegion(deviceRegionSelectOrDeleteRequest));
    }

}
