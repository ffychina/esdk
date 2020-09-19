package com.esdk.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esdk.esdk;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

public class TestOrientDB{

	public static void main(String[] args){

		OrientDB orient=new OrientDB("remote:orientdb.server",OrientDBConfig.defaultConfig());
		ODatabaseSession db=orient.open("test1","root","sys123");
		createSchema(db);
		//createPeople(db);
		executeAQuery(db);
		executeAnotherQuery(db);

		db.close();
		orient.close();

	}

	private static void createSchema(ODatabaseSession db){
		OClass person=db.getClass("Person");

		if(person==null){
			person=db.createVertexClass("Person");
		}

		if(person.getProperty("name")==null){
			person.createProperty("name",OType.STRING);
			person.createIndex("Person_name_index",OClass.INDEX_TYPE.UNIQUE,"name");
		}

		if(db.getClass("FriendOf")==null){
			db.createEdgeClass("FriendOf");
		}
	}

	private static void createPeople(ODatabaseSession db){
		OVertex alice=createPerson(db,"Alice","Foo",22,esdk.time.valueOf("1970-01-13 12:59:59"));
		OVertex bob=createPerson(db,"Bob","Bar",30,esdk.time.valueOf("1979-04-26 13:00:03"));
		OVertex jim=createPerson(db,"Jim","Baz",41,esdk.time.valueOf("1970-06-12 09:30:00"));
		OEdge edge1=alice.addEdge(bob,"FriendOf");
		edge1.save();
		OEdge edge2=bob.addEdge(jim,"FriendOf");
		edge2.save();
	}

	private static OVertex createPerson(ODatabaseSession db,String name,String surname,int age,Date birthday){
		OVertex result=db.newVertex("Person");
		result.setProperty("name",name);
		result.setProperty("surname",surname);
		result.setProperty("age",age);
		result.setProperty("birthday",birthday);
		result.save();
		return result;
	}

	private static void executeAQuery(ODatabaseSession db){
		String query="SELECT expand(out('FriendOf').out('FriendOf')) from Person where name = ?";
		OResultSet rs=db.query(query,"Alice");

		while(rs.hasNext()){
			OResult item=rs.next();
			System.out.println("friend: "+item.getProperty("friend")+", age:"+item.getProperty("age")+",birthday:"+item.getProperty("birthday"));
		}

		rs.close(); //REMEMBER TO ALWAYS CLOSE THE RESULT SET!!!
	}

	private static void executeAnotherQuery(ODatabaseSession db){
		String query=" MATCH {class:Person, as:a, where:(name = :name1)},{class:Person, as:b, where: (name = :name2)}, {as:a} -FriendOf-> {as:x} -FriendOf-> {as:b} RETURN x.name as friend";

		Map<String,Object> params=new HashMap<String,Object>();
		params.put("name1","Alice");
		params.put("name2","Jim");

		OResultSet rs=db.query(query,params);

		while(rs.hasNext()){
			OResult item=rs.next();
			System.out.println("friend: "+item.getProperty("friend")+", age:"+item.getProperty("age")+",birthday:"+item.getProperty("birthday"));
		}

		rs.close();
	}
}
