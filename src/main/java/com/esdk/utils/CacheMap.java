package com.esdk.utils;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.esdk.esdk;

/**
 * 用来存储短暂对象的缓存类，实现Map接口，内部有一个定时器用来清除过期（30秒）的对象。
 * 为避免创建过多线程，没有特殊要求请使用getDefault()方法来获取本类的实例。
 * 
 * @author www.zuidaima.com
 * @param <K>
 * @param <V>
 */

public class CacheMap<K,V>extends AbstractMap<K,V>{

	public static final long DEFAULT_TIMEOUT=60*15*1000; // 单位毫秒，默认15分钟
	private static CacheMap<Object,Object> defaultInstance;

	public static synchronized final CacheMap<Object,Object> getDefault(){
		if(defaultInstance==null){
			defaultInstance=new CacheMap<Object,Object>(DEFAULT_TIMEOUT);
		}
		return defaultInstance;
	}

	private class CacheEntry implements Entry<K,V>{
		long time;
		V value;
		K key;

		CacheEntry(K key,V value){
			super();
			this.value=value;
			this.key=key;
			this.time=System.currentTimeMillis();
		}

		@Override
		public K getKey(){
			return key;
		}

		@Override
		public V getValue(){
			return value;
		}

		@Override
		public V setValue(V value){
			return this.value=value;
		}
	}

	private long cacheTimeout=DEFAULT_TIMEOUT;
	private ConcurrentHashMap<K,CacheEntry> map=new ConcurrentHashMap<K,CacheEntry>();

	public CacheMap(){
		this.cacheTimeout=DEFAULT_TIMEOUT;
	}

	public CacheMap(long timeout){
		this.cacheTimeout=timeout;
	}

	@Override
	public Set<Entry<K,V>> entrySet(){
		Set<Entry<K,V>> entrySet=new HashSet<Map.Entry<K,V>>();
		Set<Entry<K,CacheEntry>> wrapEntrySet=map.entrySet();
		for(Entry<K,CacheEntry> entry:wrapEntrySet){
			entrySet.add(entry.getValue());
		}
		return entrySet;
	}

	private long lastCleanCacheTime=System.currentTimeMillis(),lastCacheSize=1000;
	@Override public V get(Object key){
		V result=null;
		long now=System.currentTimeMillis();
		if(map.size()>lastCacheSize && now-lastCleanCacheTime>cacheTimeout) {
			for(Iterator iter=map.entrySet().iterator();iter.hasNext();) {
				Entry<K,CacheEntry> entry=(Entry)iter.next();
				CacheEntry cacheEntry=entry.getValue();
				if(now-cacheEntry.time>cacheTimeout) {
					map.remove(cacheEntry.key);
				}
			}
			lastCleanCacheTime=now;
			lastCacheSize=map.size()*2;
			System.out.println("缓存清理后size为"+map.size());
		}
		CacheEntry entry=map.get(key);
		if(entry==null)
			result=null;
		else if(now-entry.time<cacheTimeout){
			result=entry.value;
		}else{
			map.remove(key);
			result=null;
		}
		return result;
	}

	@Override
	public V put(K key,V value){
		CacheEntry entry=new CacheEntry(key,value);
		map.put(key,entry);
		if(map.size()>lastCacheSize)
			System.out.println(esdk.str.format("CacheMap put count:{},key:{},value:{},lastCacheSize:{},lastCleanCacheTime:{}"
					,map.size(),key,value,lastCacheSize,lastCleanCacheTime)); //TODO 实际环境测试通过后要注释
		return value;
	}
	
	@Override
	public void clear(){
		map.clear();
	}

}