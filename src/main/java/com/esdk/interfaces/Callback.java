package com.esdk.interfaces;

@FunctionalInterface
public interface Callback<E>{
	void invoke(E res);
}
