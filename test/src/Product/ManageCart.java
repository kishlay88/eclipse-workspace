package Product;

import java.util.ArrayList;

import org.bson.BSON;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.CreateCollectionOperation;

public class ManageCart extends MongoContext {

	String CartID = null;
	Integer Cart_total = null;
	Integer Discount = null;

	ManageCart.CartItems mCartItems = null;

	class CartItems {
		String ItemID = null;
		String DeliveryAddress = null;
		Integer Quantity = null;
		Integer itemPrice = null;
	}

	public ManageCart() {

	}

	@SuppressWarnings("unused")
	public void populateCart(String CartID, String ItemID, String DeliveryAddress, Integer Quantity,
			Integer itemPrice) {

		mCartItems = this.new CartItems();
		mCartItems.ItemID = ItemID;
		mCartItems.DeliveryAddress = DeliveryAddress;
		mCartItems.Quantity = Quantity;
		mCartItems.itemPrice = itemPrice;

		MongoCollection<?> collection = getCollC("UserProfile");
		org.bson.Document cart = new org.bson.Document();
		org.bson.Document cart1 = new org.bson.Document();

		cart.append("CARTID", CartID).append("ItemID", mCartItems.ItemID)
				.append("DeliveryAdress", mCartItems.DeliveryAddress).append("Quantity", mCartItems.Quantity)
				.append("ItemPrice", mCartItems.itemPrice);

		cart1.append("CART", cart);
		org.bson.Document cartF = new org.bson.Document("$set", cart1);
		org.bson.Document filter = new org.bson.Document();

		collection.updateOne(Filters.eq("EMAILID", "kkumar0708@gmail.com"), cartF);
		// document.put("price", mCartItems.itemPrice);

	}

}
