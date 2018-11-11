package Product;

import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class cartMain {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		UserProfile uProfile = new UserProfile();
		
		uProfile._id = "1111";
		String add[] = {"560045" , "560024" , "834005"};
		uProfile.ADDRESS=Arrays.asList(add);
		uProfile.DbID=(long) 1;
		uProfile.DOB="07/08/1988";
		uProfile.EMAILID="kkumar0708@gmail.com";
		uProfile.NAME="Kishlay";
		uProfile.USERID="kkumar0708";
		uProfile.PASSWORD="password";
		
		ITEMS items = new ITEMS();
		ITEMS items2 = new ITEMS();
		
		items.ItemID = "1";
		items.ItemName = "a";
		items.ItemPrice = 2000;
		items.ItemQuantity = 2;
		
		items2.ItemID = "2";
		items2.ItemName = "b";
		items2.ItemPrice = 3000;
		items2.ItemQuantity = 5;
		
		
		CART cart = new CART();
		CART cart2 = new CART();
		
		cart.items.add(items);
		cart.items.add(items2);
		
		uProfile.Cart.add(cart);
		uProfile.Cart.add(cart2);
		
		Mongo mongo = new Mongo(ProjectConstants.MServerIP, 27017);
		DB db = mongo.getDB("admin");
        DBCollection mColl = db.getCollection("UserProfile");
		Gson gson = new Gson();
		
		java.lang.reflect.Type type = new TypeToken<UserProfile>() {}.getType();
		
		String json = gson.toJson(uProfile, type);
		
		System.out.println(json);
		DBObject object = (DBObject) JSON.parse(json);
		
		BasicDBObject bObject = new BasicDBObject().append("EMAILID" , "kkumar0708@gmail.com");
		mColl.update(bObject , object);
		
		//mColl.insert(object);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("EMAILID", "kkumar0708@gmail.com");
		DBCursor cursor = mColl.find(whereQuery);
		
		Document bObject2 =  new Document(cursor.next().toMap());
		
	
		String contentString =  bObject2.toString();
		
		/*
		
		UserProfile uProfile2 = new UserProfile();
		
		uProfile2.Cart = gson.fromJson(contentString, CART.class);*/
		
		Response resp = new Response();
		
		System.out.println((Map<String, Map<String, Object>>) gson.fromJson(contentString, cart.getClass()));
	
		mongo.close();
		
		
	}

}
