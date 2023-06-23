package com.exithere.rain.service;

import com.exithere.rain.entity.Region;
import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import com.exithere.rain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    @Transactional
    public Region findRegion(String regionName, int regionX, int regionY){

        Optional<Region> existRegion = regionRepository.findByRegionNameAndRegionXAndRegionY(regionName, regionX, regionY);
        if(existRegion.isPresent()){
            return existRegion.get();
        }
        else{
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

    @Transactional
    public void saveRegion(String regionName, int regionX, int regionY){
        Optional<Region> existRegion = regionRepository.findByRegionNameAndRegionXAndRegionY(regionName, regionX, regionY);
        if(existRegion.isEmpty()){
            Region region = Region.builder().regionName(regionName).regionX(regionX).regionY(regionY).build();

            regionRepository.save(region);
        }
    }
}
