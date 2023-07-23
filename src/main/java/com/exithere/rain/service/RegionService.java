package com.exithere.rain.service;

import com.exithere.rain.dto.response.RegionResponse;
import com.exithere.rain.entity.Region;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional
    public Region findRegion(String regionName, double regionX, double regionY){
        // 위경도 -> 좌표(grid) 변환
        Map<String, Integer> grid = this.transGrid(regionX, regionY);

        Optional<Region> existRegion = regionRepository.findByRegionNameAndRegionXAndRegionY(regionName, grid.get("x"), grid.get("y"));
        if(existRegion.isPresent()){
            return existRegion.get();
        }
        else{
            // 지역이 없으니 추가
            Region region = Region.builder().regionName(regionName).regionX(grid.get("x")).regionY(grid.get("y")).build();

            Region saveRegion = regionRepository.save(region);
            return saveRegion;
        }
    }

    private Map<String, Integer> transGrid(double regionX, double regionY){
        double RE = 6371.00877; // 지구반경
        double GRID = 5.0; // 격자 간격
        double SLAT1 = 30.0; // 투영 위도1
        double SLAT2 = 60.0; // 투영 위도2
        double OLON = 126.0; // 기준점 경도
        double OLAT = 38.0; // 기준점 위도
        double XO = 43; // 기준점 x좌표
        double YO = 136; // 기준점 y좌표

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + regionX * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = regionY * DEGRAD - olon;
        if(theta > Math.PI){
            theta -= 2.0 * Math.PI;
        }
        if(theta < -Math.PI){
            theta += 2.0 * Math.PI;
        }

        theta *= sn;

        double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        return Map.of("x", (int) Math.round(x), "y", (int) Math.round(y));
    }

    @Transactional(readOnly = true)
    public Region findByRegionId(int regionId){
        Optional<Region> existRegion = regionRepository.findById(regionId);
        if(existRegion.isEmpty()){
            throw new CustomException(ErrorCode.REGION_NOT_FOUND);
        }
        else{
            return existRegion.get();
        }
    }
}
