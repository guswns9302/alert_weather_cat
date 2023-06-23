package com.exithere.rain.dto.response;

import com.exithere.rain.entity.Device;
import com.exithere.rain.entity.Region;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Setter
@Getter
@Builder
@ToString
public class RegionListResponse {

    private Region selectRegion;
    private ArrayList<Region> regionList;

    public static RegionListResponse from(Device device){
        ArrayList<Region> regions = new ArrayList<>();
        if(device.getFirstRegionCd() != null){
            regions.add(device.getFirstRegionCd());
        }
        if(device.getSecondRegionCd() != null){
            regions.add(device.getSecondRegionCd());
        }
        if(device.getThirdRegionCd() != null){
            regions.add(device.getThirdRegionCd());
        }

        return RegionListResponse.builder()
                .selectRegion(device.getSelectRegionCd())
                .regionList(regions)
                .build();
    }
}
