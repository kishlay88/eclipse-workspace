package Product;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

class MongoContext {

	ProductInfo pInfo = new ProductInfo();
	
	public final MongoClient DBConnect() throws UnknownHostException {

		MongoClient mongo = new MongoClient("192.168.56.104", 27017);
		return mongo;

	}

	public final MongoCollection<org.bson.Document> getCollC() throws UnknownHostException {
		//MongoClient mongo = DBConnect();
		MongoCollection<org.bson.Document> collection = pInfo.mongo.getDatabase("admin").getCollection("Product");
		return collection;
	}

	public final void DisplayInventory() throws UnknownHostException {

		//MongoClient mongo = DBConnect();
		MongoCollection collection = getCollC();

		FindIterable<org.bson.Document> findIterable = collection.find();

		MongoCursor<Document> iterable = findIterable.iterator();

		while (iterable.hasNext())
			System.out.println(iterable.next());

	}

	public final boolean UpdateProduct(String PrimID, String UpdateField, String value) throws UnknownHostException {

		//MongoClient mongo = DBConnect();
		MongoCollection collection = getCollC();

		try {
			collection.updateOne(Filters.eq("ID", Integer.parseInt(PrimID)),
					new org.bson.Document("$set", (new org.bson.Document(UpdateField, Integer.parseInt(value)))));
		} catch (Exception e) {
			//mongo.close();
			return false;
		}
		
		//mongo.close();
		return true;
	}

	public final void FindProduct(String PrimID) throws UnknownHostException {

		//MongoClient mongo = DBConnect();
		MongoCollection collection = getCollC();

		ArrayList<org.bson.Document> Prod = (ArrayList<org.bson.Document>) collection
				.find(new org.bson.Document("ID", Integer.parseInt(PrimID))).into(new ArrayList<org.bson.Document>());

		System.out.println(Prod);
		
	}

}
