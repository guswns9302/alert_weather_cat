package com.exithere.rain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    private static final long serialVersionUID = 5742209104257968540L;

    private final ErrorCode errorCode;
}
