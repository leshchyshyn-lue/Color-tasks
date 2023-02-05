package com.example.colortasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MustContainException extends  Exception{
    public MustContainException(String message){
        super(message);
    }
}
