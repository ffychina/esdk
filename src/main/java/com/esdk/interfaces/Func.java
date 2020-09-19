package com.esdk.interfaces;

@FunctionalInterface
public interface Func<PARM,RTN>{
	RTN invoke(PARM res);
}
