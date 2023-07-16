package com.exithere.rain.entity.enumeration;

import com.exithere.rain.exception.CustomException;
import com.exithere.rain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum TargetDay {
    TODAY(0), TOMORROW(1), NONE(-1);

    private int index;

    public static TargetDay find(int index){
        Optional<TargetDay> findIndex = Arrays.stream(values()).filter(value -> value.index == index).findAny();
        if(findIndex.isPresent()){
            return findIndex.get();
        }
        else{
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

}
