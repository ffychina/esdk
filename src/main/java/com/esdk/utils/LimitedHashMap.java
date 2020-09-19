/**
*HashMap that can restrict size,when item quantity more then limitedsize.clear when invoked put method.
*/
package com.esdk.utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class LimitedHashMap<K,V> extends LinkedHashMap<K,V>{
  private static final long serialVersionUID=-5965444975016560820L;
  int _limitedSize;
  public LimitedHashMap(){
    super();
    _limitedSize=108;
  }

  public LimitedHashMap(int limitedSize){
    super(limitedSize);
    _limitedSize=limitedSize;
  }
  
  public LimitedHashMap(int size,int limitedSize){
    super(size);
    _limitedSize=limitedSize;
  }
  
  @Override public V put(K key,V value){
  	if(super.size()>_limitedSize) {
  		int removeSize=(int)_limitedSize*7/10;
  		for(Object item:this.keySet().toArray()){
				this.remove(item);
				if(removeSize<=0)
					break;
			}
  	}
    return super.put(key,value);
  }
  
  /**
   * set the limited size,default is 50.
   */
  public int limitedsize() {
    return _limitedSize;
  }
  
  @Override
  public int size() {
    return super.size();
  }
}
