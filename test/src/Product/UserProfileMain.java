package Product;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class UserProfileMain extends MongoContext {

	static MongoClient mongoUP = null;
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
			throws UnknownHostException {

		userDetails uDetails = new UserProfileMain().new userDetails();
		uDetails.Name = nString;
		uDetails.UsrID = u;
		uDetails.passwrd = pass;
		uDetails.EmailID = eid;
		uDetails.address = add;

		org.bson.Document userDetails = new org.bson.Document();
		org.bson.Document AddressD = new org.bson.Document();

		userDetails.put("NAME", uDetails.Name);
		userDetails.put("USERID", uDetails.UsrID);
		userDetails.put("PASSWORD", uDetails.passwrd);
		userDetails.put("EMAILID", uDetails.EmailID);

		Iterator<String> itr = uDetails.address.iterator();
		String elem = "0";

		while (itr.hasNext()) {
			AddressD.put(elem, itr.next());
			elem = ((Integer) (Integer.parseInt(elem) + 1)).toString();
		}

		userDetails.put("ADDRESS", AddressD);
		MongoCollection collection = getCollC("UserProfile");

		ArrayList<org.bson.Document> Prod = (ArrayList<org.bson.Document>) collection
				.find(new org.bson.Document("EMAILID", uDetails.EmailID)).into(new ArrayList<org.bson.Document>());

		if (Prod.isEmpty()) {
			try {
				collection.insertOne(userDetails);
			} catch (Exception e) {
				return 0;
			}

			return 1;
		} else {
			System.out.println("User info already Present");
			return 3;
		}

	}

	public boolean validateUser(String eID, String Passwd) {

		return false;

	}

}
