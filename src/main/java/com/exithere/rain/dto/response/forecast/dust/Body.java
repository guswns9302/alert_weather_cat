package com.exithere.rain.dto.response.forecast.dust;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Body {

    private String dataType;
    private List<Items> items = new ArrayList<>();
    private int pageNo;
    private int numOfRows;
    private int totalCount;

}
