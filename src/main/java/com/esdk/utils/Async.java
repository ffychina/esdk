package com.esdk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author ffychina
 * @since 2020-04-13
 * https://blog.csdn.net/ffychina
 * */
public class Async{
	private List<AsyncFunction> asyncFnList=new ArrayList<Async.AsyncFunction>();

	public Async(){
	}

	public Async(AsyncFunction runable){
		add(runable);
	}

	public Async add(AsyncFunction runable){
		asyncFnList.add(runable);
		return this;
	}

	public Object await(){
		CountDownLatch latch=new CountDownLatch(asyncFnList.size());
		for(AsyncFunction asyncFn:asyncFnList){
			asyncFn._latch[0]=latch;
			Thread thread=new Thread(asyncFn);
			thread.start();
		}
		try{
			latch.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		List result=this.asyncFnList.get(0)._result;
		if(asyncFnList.size()==0){
			return result.get(0);
		}else{
			return result.toArray();
		}
	}

	public static interface AsyncFunction extends Runnable{
		Object call();

		List _result=new ArrayList();
		CountDownLatch[] _latch=new CountDownLatch[1];

		@Override
		default void run(){
			synchronized(_result){
				try{
					_result.add(call());
				}catch(Exception e){
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
			_latch[0].countDown();
		}

		default Object result(){
			return _result.toArray(new Object[0]);
		}
	}

}

