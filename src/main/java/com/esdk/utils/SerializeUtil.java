package com.esdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil{
	public static byte[] serialize(Object object){
		try(ByteArrayOutputStream baos=new ByteArrayOutputStream();ObjectOutputStream oos=new ObjectOutputStream(baos)){
			oos.writeObject(object);
			oos.flush();
			byte[] bytes=baos.toByteArray();
			return bytes;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static Object unserialize(byte[] bytes){
		try(ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
				CustomObjectInputStream co=new CustomObjectInputStream(bais, Thread.currentThread().getContextClassLoader())){
			//ObjectInputStream ois=new ObjectInputStream(bais);
			//return ois.readObject();
			Object obj = co.readObject();
			co.close();
			return obj;
		}catch(Exception e){

		}
		return null;
	}
}
