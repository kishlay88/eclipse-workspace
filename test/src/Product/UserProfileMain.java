package Product;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class UserProfileMain extends MongoContext {

	static MongoClient mongoUP = null;
	static long UserCount=0;
	ProductInfo pInfo = new ProductInfo();
	ManageCart mCart = new ManageCart();

	class userDetails {
		String Name = null;
		String UsrID = null;
		String passwrd = null;
		String EmailID = null;
		String DOB = null;
		List<String> address = new ArrayList<String>();

	}

	public int PupolateNewUserDetails(String nString, String u, String pass, String eid, String dob, List<String> add)
			throws UnknownHostException, ParseException {

		userDetails uDetails = new UserProfileMain().new userDetails();
		uDetails.Name = nString;
		uDetails.UsrID = u;
		uDetails.passwrd = pass;
		uDetails.EmailID = eid;
		uDetails.address = add;
		uDetails.DOB = dob;

		org.bson.Document userDetails = new org.bson.Document();
		org.bson.Document AddressD = new org.bson.Document();

		userDetails.put("DbID", ++UserCount);
		userDetails.put("NAME", uDetails.Name);
		userDetails.put("USERID", uDetails.UsrID);
		userDetails.put("PASSWORD", uDetails.passwrd);
		userDetails.put("EMAILID", uDetails.EmailID);
		
		if(!uDetails.DOB.equals(" "))
			userDetails.put("DOB", new SimpleDateFormat("yyyy-mm-dd").parse(uDetails.DOB));
		
		
		Iterator<String> itr = uDetails.address.iterator();
		String elem = "0";

		while (itr.hasNext()) {
			AddressD.put(elem, itr.next());
			elem = ((Integer) (Integer.parseInt(elem) + 1)).toString();
		}

		userDetails.put("ADDRESS", AddressD);
		System.out.println(userDetails);
		MongoCollection<Document> collection = getCollC("UserProfile");

		ArrayList<org.bson.Document> Prod = (ArrayList<org.bson.Document>) collection
				.find(new org.bson.Document("EMAILID", uDetails.EmailID)).into(new ArrayList<org.bson.Document>());

		if (Prod.isEmpty()) {
			try {
				collection.insertOne(userDetails);
			} catch (Exception e) {
				//collection.updateOne(new org.bson.Document("EMAILID", uDetails.EmailID), userDetails);
				return 0;
			}

			return 1;
		} else {
			System.out.println("User info already Present");
			return 3;
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean validateUser(String eID, String Passwd) {

		MongoCollection collection = getCollC("UserProfile");
		ArrayList<org.bson.Document> Prod = (ArrayList<org.bson.Document>) collection
				.find(new org.bson.Document("EMAILID", eID)).into(new ArrayList<org.bson.Document>());
		
		if(Prod.get(0).get("PASSWORD").equals(Passwd)) 
			//System.out.println("Valid User"); 
			return true;
		else { 
			System.out.println("Invalid User");
			return false;
		}	
	}
	

}
