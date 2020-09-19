/**
*arraylist that can restrict size,when item quantity more then limitedsize,before will auto delete,when achieve limited size,item will roll.
*/

package com.esdk.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.esdk.exception.SdkRuntimeException;
import com.esdk.interfaces.IRequestClose;

public class LimitedList extends LinkedList{
	private static final long serialVersionUID=-104167609171896808L;
	int _limitedSize;

	public LimitedList(){
		super();
		_limitedSize=16;
	}

	public LimitedList(int limitedSize){
		super();
		_limitedSize=limitedSize;
	}

	public LimitedList(Collection c){
		super(c);
	}

	@Override
	public boolean add(Object o){
		if(isExceedLimitedSize(super.size())){
			removeOld();
		}
		return super.add(o);
	}

	/**
	 * set the limited size,default is 50.
	 */
	public int limitedsize(){
		return _limitedSize;
	}

	@Override
	public int size(){
		if(super.size()<_limitedSize)
			return super.size();
		return _limitedSize;
	}

	@Override
	public Iterator iterator(){
		removeOld();
		return super.iterator();
	}

	public void removeOld(){
		if(isExceedLimitedSize(super.size())){
			int removesize=super.size()-_limitedSize;
			for(int i=0;i<removesize;i++){
				Object obj=super.remove();
				if(obj instanceof IRequestClose){
					try{
						((IRequestClose)obj).close();
					}catch(Exception e){
						throw new SdkRuntimeException(e);
					}
				}
			}
		}
	}

	private boolean isExceedLimitedSize(int index){
		return index+1>_limitedSize;
	}

	@Override
	public boolean contains(Object elem){
		removeOld();
		return super.contains(elem);
	}

	@Override
	public int indexOf(Object elem){
		removeOld();
		return super.indexOf(elem);
	}

	@Override
	public int lastIndexOf(Object elem){
		removeOld();
		return super.lastIndexOf(elem);
	}

	@Override
	public Object set(int index,Object element){
		removeOld();
		return super.set(index,element);
	}

	@Override
	public boolean containsAll(Collection c){
		removeOld();
		return super.containsAll(c);
	}

	public static void main(String[] args){
		LimitedList ll=new LimitedList(2);
		ll.add("a");
		ll.add("b");
		ll.add("c");
		System.out.println(ll.size()==2);
		Iterator iterator=ll.iterator();
		System.out.println(iterator.next().equals("b"));
		System.out.println(iterator.next().equals("c"));
		System.out.println(!iterator.hasNext());
		System.out.println(ll.indexOf("a")<0);
		try{
			ll.set(2,"c");
		}catch(IndexOutOfBoundsException e){
			System.out.println(true);
		}
	}
}
