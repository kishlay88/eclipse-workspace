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

	//MongoClient mongo = null;
	static final MongoClient mongo = new MongoClient("192.168.56.104", 27017); ;

	public final static MongoCollection<org.bson.Document> getCollC(String Coll)  {
		// MongoClient mongo = DBConnect();
		MongoClient mongo = new MongoClient("192.168.56.104", 27017);
		MongoCollection<org.bson.Document> collection = mongo.getDatabase("admin").getCollection(Coll);
		return collection;
	}

	public final void DisplayInventory() throws UnknownHostException {


		MongoCollection collection = getCollC("Product");
		FindIterable<org.bson.Document> findIterable = collection.find();
		MongoCursor<Document> iterable = findIterable.iterator();

		while (iterable.hasNext())
			System.out.println(iterable.next());

	}

	public final boolean UpdateProduct(String PrimID, String UpdateField, String value) throws UnknownHostException {

		MongoCollection collection = getCollC("Product");

		try {
			collection.updateOne(Filters.eq("ID", Integer.parseInt(PrimID)),
					new org.bson.Document("$set", (new org.bson.Document(UpdateField, Integer.parseInt(value)))));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public final void FindProduct(String PrimID) throws UnknownHostException {

		MongoCollection collection = getCollC("Product");

		ArrayList<org.bson.Document> Prod = (ArrayList<org.bson.Document>) collection
				.find(new org.bson.Document("ID", Integer.parseInt(PrimID))).into(new ArrayList<org.bson.Document>());

		System.out.println(Prod);
	}

}
