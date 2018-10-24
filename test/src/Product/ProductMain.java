package Product;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProductMain {

	static MongoContext mContext = new MongoContext();
	static ProductInfo pInfo = new ProductInfo();
	static UserProfileMain uMain = new UserProfileMain();

	public static void main(String[] args) throws UnknownHostException, ParseException {
		
	/*
	 * mContext.FindProduct("10");
	 *  
	 * if(mContext.UpdateProduct("10", "PRICE", "10"))
	 * System.out.println("Product data updated Successfully"); 
	 * else
	 * System.out.println("Product data updation failed");
	 * 
	 * mContext.DisplayInventory();
	 */
		
	String[] address = { "560045", "560024", "834005" };
	List<String> laddr = Arrays.asList(address);

	uMain.PupolateNewUserDetails("Kishlay","kkumar0708","password","kkumar0708@gmail.com"," ",laddr);
	uMain.PupolateNewUserDetails("Swati","swati01.bitm","killer0123","swati01.bitm@gmail.com","1994-05-21",laddr);

	Random r = new Random();
	if(uMain.validateUser("kkumar0708@gmail.com","password"))
	{
		ManageCart mCart = new ManageCart();
		String CartID = ((Long) r.nextLong()).toString();
		mCart.populateCart(CartID, "1", "560045", 2, 2000);

	}

}

}
