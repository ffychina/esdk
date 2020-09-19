package com.esdk.utils;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.esdk.esdk;

import cn.hutool.core.util.ArrayUtil;

public class MString{
	LinkedList list=new LinkedList();
	char leftBracket,rightBracket;
	private boolean isReplaceBreak=false;
	private Map<Character,Character> _brackets=new HashMap<Character,Character>();
	{
		_brackets.put(')','(');
		_brackets.put(']','[');
		_brackets.put('}','{');
	}
	public void setBrackets(Map<Character,Character> map) {
		_brackets=map;
	}
	
	public void setBracket(char left,char right) {
		_brackets.put(left,right);
	}
	
	public MString(CharSequence cs){
		this.parse(cs);
	}

	private char getLeftBracket(char ch){
		return (Character)EasyObj.or(_brackets.get(ch),'\0');
	}
	
	public MString parse(CharSequence cs) {
		CharSequence str=cs;
		Deque<Character> stack=new ArrayDeque<Character>();
		Character[] leftBrackets=_brackets.values().toArray(new Character[0]);
		Character[] rightBrackets=_brackets.keySet().toArray(new Character[0]);
		if(str.length()>=2&&ArrayUtil.contains(leftBrackets,str.charAt(0))&&ArrayUtil.contains(rightBrackets,str.charAt(str.length()-1))){
			leftBracket=str.charAt(0);
			rightBracket=str.charAt(str.length()-1);
			str=str.subSequence(1,str.length()-1);
		}
		StringBuilder token=new StringBuilder();
		for(int i=0,n=str.length();i<n;i++){
			char ch=str.charAt(i);
			token.append(ch);
			if(ArrayUtil.contains(leftBrackets,ch)){
				if(stack.isEmpty()){
					list.add(token.deleteCharAt(token.length()-1));
					token=new StringBuilder().append(ch);
				}
				stack.push(ch);
			}else if(ArrayUtil.contains(rightBrackets,ch)){
				if(stack.peekFirst()!=null&&stack.peekFirst()==getLeftBracket(ch)){
					stack.poll();
					if(stack.isEmpty()){
						try{
							Constructor c=this.getClass().getConstructor(CharSequence.class);
							MString subMString=(MString)c.newInstance(token);
							list.add(subMString);
						}catch(Exception e){
							throw new RuntimeException(e);
						}
						token=new StringBuilder();
					}
				}else{
					break;
				}
			}else if(i==n-1){
				list.add(token);
			}
		}
		return this;
	}
	
	protected CharSequence replace(String regex,String replacement,final CharSequence item,final MString scope){
		esdk.tool.read(scope);
		return item.toString().replaceAll(regex,replacement);
	}
	
	public MString replaceAll(String regex,String replacement){
		for(int i=0;i<list.size();i++){
			Object item=list.get(i);
			Object rp=(item instanceof MString)?((MString)item).replaceAll(regex,replacement):replace(regex,replacement,(CharSequence)item,this);
			if(rp!=item){
				list.add(i,rp);
				list.remove(i+1);
			}
			if(this.isReplaceBreak) {
				isReplaceBreak=false;
				break;
			}
		}
		return this;
	}

	public void replaceBreak(){
		this.isReplaceBreak=true;
	}
	
	@Override public String toString(){
		StringBuilder result=new StringBuilder();
		if(leftBracket!='\0')
			result.append(leftBracket);
		for(Iterator iter=list.iterator();iter.hasNext();){
			result.append(iter.next().toString());
		}
		if(rightBracket!='\0')
			result.append(rightBracket);
		return result.toString();
	}

	public Object remove(int index) {
		return list.remove(index);
	}
	
	public Object remove(Object o) {
		return list.remove(o);
	}
	
	public MString add(CharSequence cs) {
		list.add(cs);
		return this;
	}
	
	public MString add(int index,CharSequence cs) {
		list.add(index,cs);
		return this;
	}
	
	public static void test() {
		String test="ab12+[b2(eef)w-45tyf[lk6{zxc29hg}]]";
		esdk.tool.assertEquals(new MString(test).toString(),test);
		esdk.tool.assertEquals(new MString(test).toString(),test);
		esdk.tool.assertEquals(new MString(test).replaceAll("e{2}","cd").toString(),test.replaceAll("e{2}","cd"));
	}
	public static void main(String[] args){
		test();
	}

}
