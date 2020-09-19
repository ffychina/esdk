package com.esdk.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.esdk.esdk;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisUtils{
	private static JedisPool jedisPool;
	
	static {
		JedisPoolConfig config=new JedisPoolConfig();
		config.setTestOnBorrow(false);
		config.setMaxTotal(esdk.prop.getInteger("jedis_connection_max",100));// 默认100个连接实例，如果没有释放就会卡住。
		System.out.println("jedis_connection_max is "+config.getMaxTotal());
		config.setBlockWhenExhausted(false);
		if(esdk.str.isBlank(esdk.prop.getString("jedis_password")))
			jedisPool=new JedisPool(config,esdk.prop.getString("jedis_host"),esdk.prop.getInteger("jedis_port"));
		else
			jedisPool=new JedisPool(config,esdk.prop.getString("jedis_host"),esdk.prop.getInteger("jedis_port"),Protocol.DEFAULT_TIMEOUT,esdk.prop.getString("jedis_password"));
	
	}
	
	public static JedisPool getRedisPool() {
		return jedisPool;
	}
	
	public static RedisClient getRedisClient() {
		return new RedisClient();
	}
	
	/*注意一定在尾部带*号，否则没有东西返回*/
	public static String[] keys(String pattern) {
		if(!pattern.endsWith("*"))
			pattern=pattern+"*";
		RedisClient client=getRedisClient();
		String[] result=client.getRedis().keys(pattern).toArray(new String[0]);
		/*System.out.println("getSystemEncoding:"+Tools.getSystemEncoding());
		System.out.println("getFileEncoding:"+Tools.getFileEncoding());
		String fileEncoding=Tools.getFileEncoding();	//也可以用Tools.isWindowsSystem()*/
		/*for(int i=0;i<result.length;i++) {
			System.out.println("key["+i+"]="+result[i]);
			result[i]=esdk.str.encoding(result[i],Constant.ISO8859,Constant.UTF8);
			System.out.println("gbk->utf key["+i+"]="+result[i]);
		}*/
		client.close();
		return result;
	}
	
	public static Long del(String key) {
		RedisClient client=getRedisClient();
		Long result=client.del(key);
//		System.out.println("redis del key["+key+"]");
		client.close();
		return result;
	}
	
	public static Long del(String... keys) {
		Long result=0L;
		if(keys!=null&&keys.length>0) {
			RedisClient client=getRedisClient();
			result=client.del(keys);
			client.close();
//			System.out.println("redis del keys["+sdk.str.valueOf(keys)+"]");
		}
		return result;
	}
	
	public static Long delKeys(String key) {
		RedisClient client=getRedisClient();
		Long result=client.delKeys(key);
		client.close();
		return result;
	}
	
	/**强制转为字符串保存*/
	public static void set(String key,Object value) {
		set(key,value+"");
	}
	
	public static void set(String key,String value) {
		RedisClient client=getRedisClient();
		client.set(key,value);
		client.close();
	}

	public static void set(String key,Object value,int sec) {
		set(key,value+"",sec);
	}
	
	public static void set(String key,String value,int sec) {
		RedisClient client=getRedisClient();
		client.set(key,value,sec);
//		System.out.println("redis set key["+key+"]("+sec+"秒)="+value);
		client.close();
	}
	
	public static Long ttl(String key) {
		RedisClient client=getRedisClient();
		Long result=client.ttl(key);
		System.out.println("redis del key["+key+"]");
		client.close();
		return result;
	}
	
	/**注意value不能有逗号*/
	public static void add(String key,Object value){
		String old=get(key);
		if(esdk.str.isBlank(old))
			set(key,value+"",0);
		else
			set(key,old+","+value,0);
	}
	
	/**注意value不能有逗号*/
	public static boolean remove(String key,Object value){
		String v=get(key);
		HashSet set=esdk.str.strToSet(v);
		boolean result=set.remove(value+"");
		v=esdk.str.setToStr(set);
		set(key,v);
		return result;
	}

	/**注意value不能有逗号*/
	public static boolean contains(String key,Object value){
		RedisClient client=getRedisClient();
		boolean result=client.contains(key,value);
		client.close();
		return result;
	}
	
	public static boolean exists(String key) {
		RedisClient client=getRedisClient();
		boolean result=client.getRedis().exists(key);
		client.close();
		return result;
	}

	public static long exists(String... key) {
		RedisClient client=getRedisClient();
		long result=client.exists(key);
		client.close();
		return result;
	}
	
	public static long incr(String key) {
		RedisClient client=getRedisClient();
		long result=client.incr(key);
		client.close();
		return result;
	}

	public static long incr(String key,int sec) {
		RedisClient client=getRedisClient();
		long result=client.incr(key,sec);
		client.close();
		return result;
	}
	
	public static long decr(String key) {
		RedisClient client=getRedisClient();
		long result=client.decr(key);
		client.close();
		return result;
	}
	
	public static long decr(String key,int sec) {
		RedisClient client=getRedisClient();
		long result=client.decr(key,sec);
		client.close();
		return result;
	}

	public static void setObj(String key,Object value,int sec) {
		RedisClient client=getRedisClient();
		client.setObj(key,value,sec);
		client.close();
	}
	
	public static void setObj(String key,Object value) {
		RedisClient client=getRedisClient();
		client.setObj(key,value);
		client.close();
	}
	
	public static Object getObj(String key) {
		RedisClient client=getRedisClient();
		Object result=client.getObj(key);
		client.close();
		return result;
	}

	public static Object getObj(String key,int sec) {
		RedisClient client=getRedisClient();
		Object result=client.getObj(key,sec);
		client.close();
		return result;
	}

	public static boolean existsObj(String key) {
		RedisClient client=getRedisClient();
		boolean result=client.existsObj(key);
		client.close();
		return result;
	}
	
	public static void expire(String key,int sec) {
		RedisClient client=getRedisClient();
		client.expire(key,sec);
		client.close();
	}
	
	public static String get(String key) {
		RedisClient client=getRedisClient();
		String result=client.get(key);
		client.close();
		return result;
	}

	public static String get(String key,int sec) {
		RedisClient client=getRedisClient();
		String result=client.get(key,sec);
		client.close();
		return result;
	}
	
	public static void hmset(String key,String... keyValues) {
		RedisClient client=getRedisClient();
		client.hmset(key,keyValues);
		client.close();
	}
	
	public static void hmset(String key,Map map) {
		RedisClient client=getRedisClient();
		client.hmset(key,map);
		client.close();
	}
	
	public static void hmset(String key,Map map,int sec) {
		RedisClient client=getRedisClient();
		client.hmset(key,map);
		client.expire(key,sec);
		client.close();
	}
	
	public static List<String> hmget(String key,String... fields) {
		RedisClient client=getRedisClient();
		List<String> result=client.hmget(key,fields);
		client.close();
		return result;
	}
	
	public static Long hset(String key,String field,String value,int sec) {
		RedisClient client=getRedisClient();
		Long result=client.hset(key,field,value,sec);
		client.close();
		return result;
	}
	
	public static Long hset(String key,String field,String value) {
		RedisClient client=getRedisClient();
		Long result=client.hset(key,field,value);
		client.close();
		return result;
	}
	
	public static String hget(String key,String field) {
		RedisClient client=getRedisClient();
		String result=client.hget(key,field);
		client.close();
		return result;
	}
	
	public static Long hsetObj(String key,String field,Object value,int sec) {
		RedisClient client=getRedisClient();
		Long result=client.hsetObj(key,field,value,sec);
		client.close();
		return result;
	}
	
	public Object hgetObj(String key,String field) {
		RedisClient client=getRedisClient();
		Object result=client.hgetObj(key,field);
		client.close();
		return result;
	}
	
	public static void main(String[] args) throws Exception{
		String key="TEST 我们是人。.";
		RedisUtils.set(key,"ABcd1234我们是技术部");
		String[] keys=RedisUtils.keys("*");
		System.out.println(keys.length);
		for(String item:keys) {
			System.out.println(item);
		}
		/*Tools.assertEquals((Long)RedisUtils.del(keys),(Long)1L);
		Tools.assertEquals(keys[0],key);
		keys=RedisUtils.keys("TEST *");
		Tools.assertEquals(keys.length,0);
		keys=RedisUtils.keys("SELECT *");
		System.out.println(RedisUtils.del(keys));*/
	}
}
