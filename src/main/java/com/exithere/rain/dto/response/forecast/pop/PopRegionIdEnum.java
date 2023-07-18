package com.exithere.rain.dto.response.forecast.pop;

import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum PopRegionIdEnum implements Serializable {

    SEOUL("서울특별시","11B00000"),
    INCHEON("인천광역시","11B00000"),
    KKDO("경기도","11B00000"),
    KWDKL("강원도 강릉시","11D20000"),
    KWDDH("강원도 동해시","11D20000"),
    KWDSCH("강원도 속초시","11D20000"),
    KWDSCHC("강원도 삼척시","11D20000"),
    KWDTB("강원도 태백시","11D20000"),
    KWDKS("강원도 고성군","11D20000"),
    KWDTYY("강원도 양양군","11D20000"),
    KWDW("강원도","11D10000"),
    DAEJON("대전광역시","11C20000"),
    SEJONG("세종특별자치시","11C20000"),
    CHND("충청남도","11C20000"),
    CHBD("충청북도","11C10000"),
    KJ("광주광역시","11F20000"),
    JLANAMDO("전라남도","11F20000"),
    JLABUKDO("전라북도","11F10000"),
    DAKU("대구광역시","11H10000"),
    KBUKDO("경상북도","11H10000"),
    KNADO("경상남도","11H20000"),
    BUSAN("부산광역시","11H20000"),
    USAN("울산광역시","11H20000"),
    JEJU("제주특별자치도","11G00000")
    ;

    private String regionName;
    private String regionCode;

    public static String find(String name){
        Optional<PopRegionIdEnum> findIndex = Arrays.stream(values()).filter(value -> name.contains(value.regionName)).findAny();
        if(findIndex.isPresent()){
            return findIndex.get().getRegionCode();
        }
        else{
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

    public static Set<String> get(){
        Set<String> popEnumList = new HashSet<>();

        for(PopRegionIdEnum popRegionIdEnum : values()){
            popEnumList.add(popRegionIdEnum.getRegionCode());
        }

        return popEnumList;
    }
}
