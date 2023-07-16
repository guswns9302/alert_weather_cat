package com.exithere.rain.dto.response.forecast.week;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Items {
    private List<Item> item = new ArrayList<Item>();
}
