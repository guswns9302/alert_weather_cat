package com.exithere.rain.entity.enumeration;

import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum Ratio {
    FOURTH(0), SIXTH(1), EIGHTY(2), NONE(-1);

    private int index;

    public static Ratio find(int index){
        Optional<Ratio> findIndex = Arrays.stream(values()).filter(value -> value.index == index).findAny();
        if(findIndex.isPresent()){
            return findIndex.get();
        }
        else{
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }
}
