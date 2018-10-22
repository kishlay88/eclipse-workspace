package Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductMain {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String file = "Y:\\git_repos\\eclipse-workspace\\test\\src\\Product\\productfile";
		
		BufferedReader Reader = new BufferedReader(new FileReader(file));
		String line = null;
		while((line  = Reader.readLine()) != null) {
			 	String linS[];
				linS  = line.split(":");
				ProductInfo.pInfo.add(new ProductInfo(linS[0], linS[1], linS[2], linS[3] , linS[4]));	
		}
		
		SearchProduct sProduct = new SearchProduct();
		sProduct.IsProductInStockByID(0);
		
		ChangePrice cPrice = new ChangePrice();
		//cPrice.ChangePriceByType("A" , 10 );
		cPrice.ChangePriceByID("4", 20);
		
		ProductInfo.DisplayProduct();
		
	}

}
