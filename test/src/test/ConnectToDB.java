package test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.swing.text.Document;

import com.mongodb.MongoClient; 
import com.mongodb.MongoCredential;  

public class ConnectToDB { 
   
   public static void main( String args[] ) {  
   

		   MongoClient mongoClient = new MongoClient("192.168.56.104" , 27017);
		   MongoCredential credential ;
		   credential= MongoCredential.createCredential("admin", "admin", "Qwer@1234".toCharArray());
	
		   MongoDatabase database = mongoClient.getDatabase("admin");
		   
		   MongoCollection<org.bson.Document> collection = database.getCollection("students");
   
   } 
}