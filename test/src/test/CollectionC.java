package test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CollectionC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	String string1 = "Kishlay";
		
	List<String> list1 = new LinkedList<String>();
	list1 = Arrays.asList(string1);
	List<String> list2 = new LinkedList<String>(list1);
	
	
	System.out.println(list1);
	System.out.println(list2);
		
	System.out.println(Collections.binarySearch(list1, "i"));
	
	}

}
