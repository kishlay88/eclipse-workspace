package Product;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

class ProductInfo {

	int prod_id;
	String prod_name;
	int prod_price;
	String prod_Type;
	String prod_Quantity;

	static List<ProductInfo> pInfo = new ArrayList<ProductInfo>();
	static MongoClient mongo = null;

	public ProductInfo() {
/*
		try {

			mongo = new MongoClient("192.168.56.104", 27017);

		} catch (MongoException e) {
			System.out.println("Unable to connect to the DB Server..Exiting !!!");
			System.exit(1);
		}

		if (mongo == null) {

			System.out.println("Unable to connect to the DB Server..Exiting !!!");
			System.exit(1);

		}
*/
	}

	public ProductInfo(String i, String nString, String p, String s, String Q) {
		// TODO Auto-generated constructor stub
		prod_id = Integer.parseInt(i);
		prod_name = nString;
		prod_price = Integer.parseInt(p);
		prod_Type = s;
		prod_Quantity = Q;
	}

	static void DisplayProduct() {

		Iterator<ProductInfo> iterator = ProductInfo.pInfo.iterator();

		System.out.println();
		System.out.println();
		System.out.println(">>> Products in Stock :\n");
		System.out.println("PROD_ID\t\t" + "PROD_NAME\t\t" + "PROD_PRICE\t\t" + "PROD_TYPE\t\t" + "Quantity");
		System.out.println("~~~~~~~\t\t" + "~~~~~~~~~\t\t" + "~~~~~~~~~~\t\t" + "~~~~~~~~~\t\t" + "~~~~~~~~");

		while (iterator.hasNext()) {
			ProductInfo productInfo = iterator.next();
			System.out
					.println(productInfo.prod_id + "\t\t" + productInfo.prod_name + "\t\t    " + productInfo.prod_price
							+ "\t\t\t   " + productInfo.prod_Type + "\t\t\t   " + productInfo.prod_Quantity);
		}
	}

}
