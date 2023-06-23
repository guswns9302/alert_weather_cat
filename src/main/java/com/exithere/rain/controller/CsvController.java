package com.exithere.rain.controller;

import com.exithere.rain.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/v1/csv")
@RequiredArgsConstructor
public class CsvController {

    private final RegionService regionService;

    @GetMapping("/readCsv")
    public void readCsv(){
        // 추후 return 할 데이터 목록
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        // return data key 목록
        List<String> headerList = new ArrayList<String>();

        try{
            BufferedReader br = Files.newBufferedReader(Paths.get("/Users/yunhyunjun/Downloads/test.csv"));
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
