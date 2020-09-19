package com.esdk.exception;

/**以后要改为EsdkRuntimeException*/
public class SdkRuntimeException extends RuntimeException{
	public SdkRuntimeException(Throwable e){
		super(e);
	}
	public SdkRuntimeException(String message){
		super(message);
	}
}
