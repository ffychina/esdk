package com.esdk.test;


import java.net.InetSocketAddress;
import java.net.Socket;

import org.nutz.lang.Stopwatch;

import com.esdk.esdk;

public class TestSocket {
	public static void main(String[] args) throws Exception {
		test();
	}

	private static void test() throws Exception {
		Stopwatch sw=new Stopwatch();
		InetSocketAddress endpoint=new InetSocketAddress("fastdfs.server",22122);
		Socket socket=new Socket();
		socket.connect(endpoint);
		socket.close();
		System.out.println(esdk.time.formatElapse(sw.getDuration()));
	}
}
