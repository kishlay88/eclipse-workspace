package Product;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ProductMain  {

	static MongoContext mContext = new MongoContext();
	static ProductInfo pInfo = new ProductInfo();
	static ManageCart mCart = new ManageCart();	
	static UserProfileMain uMain = new UserProfileMain();

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub

		/*
		 * mContext.FindProduct("10"); if(mContext.UpdateProduct("10", "PRICE", "10"))
		 * System.out.println("Product data updated Successfully"); else
		 * System.out.println("Product data updation failed");
		 * mContext.DisplayInventory();
		 */
		String[] address = { "560045", "560024", "834005" };
		List<String> laddr = Arrays.asList(address);

		uMain.PupolateNewUserDetails("Kishlay", "kkumar0708", "password", "kkumar0708@gmail.com", " ", laddr);
		
		
	}

}
