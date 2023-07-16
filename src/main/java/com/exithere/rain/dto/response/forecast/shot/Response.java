package com.exithere.rain.dto.response.forecast.shot;

import com.exithere.rain.dto.response.forecast.shot.Body;
import com.exithere.rain.dto.response.forecast.shot.Header;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private Header header;
    private Body body;
}
