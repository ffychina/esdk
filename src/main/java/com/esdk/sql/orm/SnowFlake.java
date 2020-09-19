package com.esdk.sql.orm;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import com.esdk.utils.TimeMeter;

public class SnowFlake {

	/**
	 * 起始的时间戳 2019-11-25 22:33:38 GMT+0800 (中国标准时间)
	 */
	private final static long START_STMP =1574692418000L;  //1574692418L;

	/**
	 * 每一部分占用的位数
	 */
	private final static long SEQUENCE_BIT = 12L; // 序列号占用的位数
	private final static long MACHINE_BIT = 5L; // 机器标识占用的位数
	private final static long DATACENTER_BIT = 5L;// 数据中心占用的位数

	/**
	 * 每一部分的最大值
	 */
	private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
	private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
	private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

	/**
	 * 每一部分向左的位移
	 */
	private final static long MACHINE_LEFT = SEQUENCE_BIT;
	private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
	private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

	private static volatile SnowFlake snowFlakeSingleInstance = null;

	private long datacenterId; // 数据中心
	private long machineId; // 机器标识
	private long sequence = 0L; // 序列号
	private long lastStmp = -1L;// 上一次时间戳
	private static Object lock = new Object();

	/**
	 * 生成1-31之间的随机数
	 *
	 * @return
	 */
	private static long getRandom() {
		int max = (int) (MAX_MACHINE_NUM);
		int min = 1;
		Random random = new Random();
		long result = random.nextInt(max - min) + min;
		return result;
	}

	public SnowFlake(long datacenterId, long machineId) {
		if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
			throw new IllegalArgumentException(
					String.format("%s 数据中心ID最大值 必须是 %d 到 %d 之间", datacenterId, 0, MAX_DATACENTER_NUM));
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			machineId = getRandom();
		}
		this.datacenterId = datacenterId;
		this.machineId = machineId;
	}

	private static long getWorkerId() throws SocketException, UnknownHostException, NullPointerException {

		InetAddress ip = InetAddress.getLocalHost();

		NetworkInterface network = null;
		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface nint = en.nextElement();
			if (!nint.isLoopback() && nint.getHardwareAddress() != null) {
				network = nint;
				break;
			}
		}

		byte[] mac = network.getHardwareAddress();

		Random rnd = new Random();
		byte rndByte = (byte) (rnd.nextInt() & 0x000000FF);

		// 取mac地址最后一位和随机数
		return ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) rndByte) << 8))) >> 6;
	}

	/**
	 * 获取单列
	 *
	 * @return
	 */
	public static SnowFlake getInstance() {
		if (snowFlakeSingleInstance == null) {
			synchronized (lock) {
				long workerId;
				long dataCenterId = getRandom();
				try {
					// 第一次使用获取mac地址的
					workerId = getWorkerId();
				} catch (Exception e) {
					workerId = getRandom();
				}
				snowFlakeSingleInstance = new SnowFlake(dataCenterId, workerId);
			}
		}
		return snowFlakeSingleInstance;
	}

	/**
	 * 产生下一个ID
	 *
	 * @return
	 */
	public synchronized long nextId() {
		long currStmp = getNewstmp();
		if (currStmp < lastStmp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currStmp == lastStmp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & MAX_SEQUENCE;
			// 同一毫秒的序列数已经达到最大
			if (sequence == 0L) {
				currStmp = getNextMill();
			}
		} else {
			// 不同毫秒内，序列号置为0
			sequence = 0L;
		}

		lastStmp = currStmp;

		return  (currStmp - START_STMP)<< TIMESTMP_LEFT  // 时间戳部分
				| datacenterId << DATACENTER_LEFT // 数据中心部分
				| machineId << MACHINE_LEFT // 机器标识部分
			  |sequence; // 序列号部分
	}

	private long getNextMill() {
		long mill = getNewstmp();
		while (mill <= lastStmp) {
			mill = getNewstmp();
		}
		return mill;
	}

	private long getNewstmp() {
		return System.currentTimeMillis();
	}
	
	//把id转换成该id的创建时间.
	public long getCreatTime(long id) {
		long idTime=id >> 22;
		long initTime=SnowFlake.START_STMP; 
		long creatTime=idTime+initTime;
		return creatTime;
		
		
	}
	public static void main(String[] args) {
		/*	long id1=SnowFlake.getInstance().nextId();
		System.out.println("id:"+id1);
		System.out.println(new Date(SnowFlake.getInstance().getCreatTime(id1)).toLocaleString());
		
		System.out.println("---------------------------------------");
		long id=SnowFlake.getInstance().getCreatTime(SnowFlake.getInstance().nextId());
		System.out.println(new Date(id).toLocaleString());*/

	}

}
