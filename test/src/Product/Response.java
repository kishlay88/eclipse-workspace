package Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {

	String _id;
	Long DbID;
	String NAME;
	String USERID;
	String PASSWORD;
	String EMAILID;
	String DOB;
	
	Map<String, Object> ADDRESS = new HashMap<String,Object>();
	
	Map<String,Map<String, Object>> Cart = new HashMap<String,Map<String, Object>>();
}
