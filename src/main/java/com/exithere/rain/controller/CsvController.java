package com.exithere.rain.controller;

import com.exithere.rain.dto.response.RegionResponse;
import com.exithere.rain.entity.Region;
import com.exithere.rain.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Tag(name = "csv to json", description = "지역 목록 및 좌표 관련 API")
@RestController
@RequestMapping("/api/v1/csv")
@RequiredArgsConstructor
public class CsvController {

    private final RegionService regionService;

    @Operation(summary = "get json of region list from csv", description = "지역 검색 목록을 만드는 지역 리스트 json")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Region.class))))
    })
    @GetMapping("/regions")
    public ResponseEntity<List<Region>> getRegionListJson(){
        return ResponseEntity.ok(regionService.getListRegion());
    }


    @Operation(summary = "호출하지 마세요ㅋㅋ")
    @GetMapping("/readCsv")
    public void readCsv(){
        // 추후 return 할 데이터 목록
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        // return data key 목록
        List<String> headerList = new ArrayList<String>();

        try{

            BufferedReader br = Files.newBufferedReader(Paths.get("/home/ubuntu/alert_weather_cat/region_list.csv"));
            String line = "";

            while((line = br.readLine()) != null){
                List<String> stringList = new ArrayList<String>();
                String stringArray[] = line.split(",");
                stringList = Arrays.asList(stringArray);

                // csv 1열 데이터를 header로 인식
                if(headerList.size() == 0){
                    headerList = stringList;
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    // header 컬럼 개수를 기준으로 데이터 set
                    for(int i = 0; i < headerList.size(); i++){
                        map.put(headerList.get(i), stringList.get(i));
                    }
                    mapList.add(map);
                }
            }

            for(Map<String, Object> dataMap : mapList){
                String regionName = dataMap.get("SI") + " " + dataMap.get("GU") + " " + dataMap.get("DONG");
                int regionX = Integer.parseInt(dataMap.get("X").toString());
                int regionY = Integer.parseInt(dataMap.get("Y").toString());

                regionService.saveRegion(regionName, regionX, regionY);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
