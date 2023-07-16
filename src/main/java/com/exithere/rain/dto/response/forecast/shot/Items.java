package com.exithere.rain.dto.response.forecast.shot;

import com.exithere.rain.dto.response.forecast.shot.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Items {
    private List<Item> item = new ArrayList<Item>();
}
