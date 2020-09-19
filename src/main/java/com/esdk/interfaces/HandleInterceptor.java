package com.esdk.interfaces;

@FunctionalInterface
public interface HandleInterceptor<E>{
	@SuppressWarnings("unused")
	default boolean preHandle(E arg) {
		return true;
	}
	boolean handle(E res);
	
	@SuppressWarnings("unused")
	default void postHandle(E arg) {
		
	}
}
