package Product;

import java.util.Iterator;

public class manageProduct {
	
		public final void IsProductInStockByID(Integer id) {
			
			Iterator<ProductInfo> iterator = ProductInfo.pInfo.iterator();
			boolean flag = false;
			
			while(iterator.hasNext()) {
				ProductInfo productInfo = iterator.next();
				if ((productInfo.prod_id) == id.intValue()){
					flag = true;
					System.out.println(">>> Product with ID =" + productInfo.prod_id + " Found : ");
					System.out.println();
					System.out.println("PROD_ID\t\t" + "PROD_NAME\t\t" + "PROD_PRICE\t\t" + "PROD_TYPE\t\t" + "Quantity");
					System.out.println("~~~~~~~\t\t" + "~~~~~~~~~\t\t" + "~~~~~~~~~~\t\t" + "~~~~~~~~~\t\t" + "~~~~~~~~");
					System.out.println(productInfo.prod_id + "\t\t" + productInfo.prod_name + "\t\t    " + productInfo.prod_price + "\t\t\t   " + productInfo.prod_Type + "\t\t\t   " + productInfo.prod_Quantity );
					System.out.println();
					break;
				}	
				
			};
			
			if(flag == false)
				System.out.println(">>> Product with ID = " + id + " is out of Stock ");
		}
}