package Product;

import java.net.UnknownHostException;
import java.util.Iterator;

import javax.swing.text.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoContext {

	public static void main(String... args) throws UnknownHostException {
		
		MongoClient mongo = new MongoClient("192.168.56.104", 27017);
		
		MongoDatabase database = mongo.getDatabase("admin");
		MongoCollection<org.bson.Document> collection = database.getCollection("Product");
		
	    FindIterable<org.bson.Document> iterDoc = collection.find(); 
	    
	    // Getting the iterator 
	    Iterator it = iterDoc.iterator(); 
	    
	    while (it.hasNext()) 
	       System.out.println(it.next());  
	    
	    mongo.close();
	}
}
