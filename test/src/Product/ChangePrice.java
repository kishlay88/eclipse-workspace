package Product;

import java.util.Iterator;

public class ChangePrice {

	public final void ChangePriceByType(String type, int p) {

		Iterator<ProductInfo> iterator = ProductInfo.pInfo.iterator();

		while (iterator.hasNext()) {
			ProductInfo productInfo = iterator.next();
			if ((productInfo.prod_Type).equals(type))
				productInfo.prod_price += p;
		}
		;
	}

	public final void ChangePriceByName(String nString, int p) {

		Iterator<ProductInfo> iterator = ProductInfo.pInfo.iterator();

		while (iterator.hasNext()) {
			ProductInfo productInfo = iterator.next();
			if ((productInfo.prod_name).equals(nString))
				productInfo.prod_price += p;
		}
		;
	}

	public final void ChangePriceByID(String nID, int p) {

		Iterator<ProductInfo> iterator = ProductInfo.pInfo.iterator();

		while (iterator.hasNext()) {
			ProductInfo productInfo = iterator.next();
			if ((productInfo.prod_id) == Integer.parseInt(nID))
				productInfo.prod_price += p;
		}
		;
	}

}
