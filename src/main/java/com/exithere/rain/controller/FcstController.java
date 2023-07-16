package com.exithere.rain.controller;

import com.exithere.rain.dto.response.DeviceTotalResponse;
import com.exithere.rain.dto.response.forecast.ForecastResponse;
import com.exithere.rain.service.FcstService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forecast")
@RequiredArgsConstructor
@Tag(name = "Forecast", description = "예보와 관련된 API")
public class FcstController {

    private final FcstService fcstService;

    @Operation(summary = "Reload forecast information", description = "스플래쉬 화면, 날씨 정보 새로고침에서 사용하는 API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ForecastResponse.class)))
    })
    @GetMapping("/reload")
    public ResponseEntity<ForecastResponse> forecastReload(@RequestParam String regionName, @RequestParam int regionX, @RequestParam int regionY){
        return ResponseEntity.ok(fcstService.reloadFcst(regionName, regionX, regionY));
    }
}
