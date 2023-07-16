package com.exithere.rain.dto.response.forecast.pop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Body {

    private String dataType;
    private Items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;

}
