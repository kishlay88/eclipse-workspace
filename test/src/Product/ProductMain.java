package Product;

import java.io.IOException;

public class ProductMain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		MongoContext mContext = new MongoContext();
		mContext.FindProduct("10");
		if(mContext.UpdateProduct("10", "PRICE", "10"))
			System.out.println("Product data updated Successfully");
		else
			System.out.println("Product data updation failed");
		//mContext.DisplayInventory();
		
	}

}
