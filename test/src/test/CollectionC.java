package test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CollectionC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	String string1 = "Kishlay kishlay kishlay sweta Sweta Swati swati";
		
	List<String> list1 = new LinkedList<String>();
	list1 = Arrays.asList(string1.toLowerCase().split("\\s+"));
	List<String> list2 = new LinkedList<String>(list1);
	
	Set<String> set = new TreeSet<String>();
	
	for(String string : list1)
		set.add(string);

	System.out.printf("%s", set);	
	
	
	
	}

}
