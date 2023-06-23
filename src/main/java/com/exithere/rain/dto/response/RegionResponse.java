package com.exithere.rain.dto.response;

import com.exithere.rain.entity.Region;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RegionResponse {

    private Long regionId;
    private String regionName;
    private int regionX;
    private int regionY;

    public static RegionResponse from(Region region){
        return RegionResponse.builder()
                .regionId(region.getRegionId())
                .regionName(region.getRegionName())
                .regionX(region.getRegionX())
                .regionY(region.getRegionY())
                .build();
    }
}
