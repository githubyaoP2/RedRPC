package com.red.api.exception;

public class RedServiceException extends RuntimeException{

    public RedServiceException(){
        super();
    }

    public RedServiceException(String message){
        super(message);
    }
}
