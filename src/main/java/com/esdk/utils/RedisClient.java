package com.esdk.utils;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esdk.esdk;
import com.esdk.sql.orm.IResultSetCursor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
public class RedisClient implements Closeable{
	private static JedisPool jedisPool=RedisUtils.getRedisPool();
	private Jedis jedis;
	private int redisDB;
	
	public RedisClient(){
		redisDB=esdk.prop.getInteger("jedis_db");
	}
	
	public RedisClient(int db) {
		redisDB=db;
	}
	
	public Jedis getRedis() {
		if(jedis==null||!jedis.isConnected()) {
//			System.out.println("NumActive["+jedisPool.getNumActive()+"], NumIdle["+jedisPool.getNumIdle()+"], NumWaiters["+jedisPool.getNumWaiters()+"]");
			jedis=jedisPool.getResource();
			jedis.select(redisDB);
//			singleton=new Jedis(esdk.getConfig().getProperty("jedis_host"),Integer.valueOf(esdk.getConfig().getProperty("jedis_port")),5000);
		}
		return jedis;
	}
	
	public void close() {
		if(jedis!=null&&jedis.isConnected()) {
			try{
				jedis.close();
			}catch(JedisException e){
				e.printStackTrace();
			}finally {
				jedis=null;
			}
		}
	}
	
	protected void finalize() throws Throwable {
		close();
	};
	
	public String[] keys(String pattern) {
		Set<byte[]> set=getRedis().keys(esdk.str.getUTF8Bytes(pattern));
		String[] result=new String[set.size()];
		int i=0;
		for(byte[] item:set) {
			result[i]=esdk.str.getUTF8String(item);
			i++;
		}
		return result;
	}
	
	public Long del(String key) {
		return getRedis().del(esdk.str.getUTF8Bytes(key));
	}
	
	public Long ttl(String key) {
		return getRedis().ttl(esdk.str.getUTF8Bytes(key));
	}
	
	public Long del(String... keys) {
		if(keys!=null&&keys.length>0)
			return getRedis().del(keys);
		return 0L;
	}
	
	public Long delKeys(String pattern) {
		long result;
		Jedis jedis=getRedis();
		if(!pattern.endsWith("*"))
			pattern=pattern+"*";
		byte[][] keys=jedis.keys(esdk.str.getUTF8Bytes(pattern)).toArray(new byte[0][]);
		if(keys.length>0) {
			result=jedis.del(keys);
		}
		else
			result=0L;
		return result;
	}
	
	public void set(String key,Object value) {
		set(key,value+"");
	}
	
	public void set(String key,String value) {
		getRedis().set(esdk.str.getUTF8Bytes(key),esdk.str.getUTF8Bytes(value));
	}
	
	/**有编码问题，强制用utf8格式保存*/
	public void set(String key,String value,int sec) {
		Jedis jedis=getRedis();
		byte[] keybytes=esdk.str.getUTF8Bytes(key);
		jedis.set(keybytes,esdk.str.getUTF8Bytes(value));
		if(sec>0)
			jedis.expire(keybytes,sec);
		/*jedis.set(key,value);
		if(sec>0)
			jedis.expire(key,sec);*/
	}
	
	public void set(String key,Object value,int sec) {
		set(key,value+"",sec);
	}
	
	/**注意value不能有逗号*/
	public void add(String key,Object value){
		String old=get(key);
		if(esdk.str.isBlank(old))
			set(key,value+"");
		else
			set(key,old+","+value);
	}
	
	/**注意value不能有逗号*/
	public boolean remove(String key,Object value){
		String v=get(key);
		HashSet set=esdk.str.strToSet(v);
		boolean result=set.remove(value+"");
		v=esdk.str.setToStr(set);
		set(key,v);
		return result;
	}

	/**注意value不能有逗号*/
	public boolean contains(String key,Object value){
		String v=get(key);
		HashSet set=esdk.str.strToSet(v);
		return set.contains(value+"");
	}
	
	public boolean exists(String key) {
		Jedis jedis=getRedis();
		return jedis.exists(key);
	}

	public long exists(String... key) {
		return getRedis().exists(key);
	}

	public long incr(String key) {
		return getRedis().incr(key);
	}
	
	public long incr(String key,int sec) {
		Jedis jedis=getRedis();
		long result=jedis.incr(key);
		jedis.expire(key,sec);
		return result;
	}
	
	public long decr(String key) {
		return getRedis().decr(key);
	}

	public long decr(String key,int sec) {
		Jedis jedis=getRedis();
		long result=jedis.decr(key);
		jedis.expire(key,sec);
		return result;
	}
	
	public RedisClient setObj(String key,Object value,int sec) {
		serialize(key,value,sec);
		return this;
	}
	
	public RedisClient setObj(String key,Object value) {
		serialize(key,value);
		return this;
	}

	public Object getObj(String key) {
		return unserialize(key);
	}
	
	public Object getObj(String key,int sec) {
		return unserialize(key,sec);
	}
	
	private void serialize(String key,Object value) {
		if(value!=null&&value instanceof IResultSetCursor)
			((IResultSetCursor)value).size();
		getRedis().set(esdk.str.getUTF8Bytes(key),SerializeUtil.serialize(value));
	}
	
	private void serialize(String key,Object value,int sec) {
		if(value!=null&&value instanceof IResultSetCursor)
			((IResultSetCursor)value).size();
		getRedis().setex(esdk.str.getUTF8Bytes(key),sec,SerializeUtil.serialize(value));
//	SerializeUtil.unserialize(SerializeUtil.serialize(value));
	}
	
	private Object unserialize(String key) {
		return SerializeUtil.unserialize(getRedis().get(esdk.str.getUTF8Bytes(key)));
	}
	
	private Object unserialize(String key,int sec) {
		Object result=SerializeUtil.unserialize(getRedis().get(esdk.str.getUTF8Bytes(key)));
		if(result!=null)
			getRedis().expire(key,sec);
		return result;
	}
	
	public boolean existsObj(String key) {
		return getRedis().exists(esdk.str.getUTF8Bytes(key));
	}
	
	public void expire(String key,int sec) {
		getRedis().expire(key,sec); //秒为单位
	}
	
	public String get(String key) {
		Jedis jedis=getRedis();
		return esdk.str.getUTF8String(jedis.get(esdk.str.getUTF8Bytes(key)));
	}
	
	/**取值并设置过期时间*/
	public String get(String key,int sec) {
		byte[] keybytes=esdk.str.getUTF8Bytes(key);
		Jedis jedis=getRedis();
		if(jedis.exists(keybytes))
			jedis.expire(keybytes,sec);
		return esdk.str.getUTF8String(jedis.get(keybytes));
	}
	
	public RedisClient hmset(String key,String... keyValues) {
		HashMap map=new HashMap(keyValues.length/2);
		for(int i=0,n=keyValues.length/2;i<n;i++) {
			map.put(keyValues[i],keyValues[i+1]);
		}
		getRedis().hmset(key,map);
		return this;
	}
	
	public RedisClient hmset(String key,Map map) {
		getRedis().hmset(key,map);
		return this;
	}
	
	public List<String> hmget(String key,String... fields) {
		return getRedis().hmget(key,fields);
	}
	
	public Long hset(String key,String field,String value) {
		return getRedis().hset(key,field,value);
	}
	
	public Long hset(String key,String field,String value,int sec) {
		Long result=getRedis().hset(key,field,value);
		getRedis().expire(key,sec);
		return result;
	}
	
	public String hget(String key,String field) {
		return getRedis().hget(key,field);
	}
	
	public Object hgetObj(String key,String field) {
		return SerializeUtil.unserialize(getRedis().hget(esdk.str.getUTF8Bytes(key),esdk.str.getUTF8Bytes(field)));
	}
	
	public Long hsetObj(String key,String field,Object value,int sec) {
		if(value!=null&&value instanceof IResultSetCursor)
			((IResultSetCursor)value).size();
		Long result=getRedis().hset(esdk.str.getUTF8Bytes(key),esdk.str.getUTF8Bytes(field),SerializeUtil.serialize(value));
		getRedis().expire(key,sec);
		return result;
	}
	
}
