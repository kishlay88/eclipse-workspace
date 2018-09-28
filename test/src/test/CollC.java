package test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CollC {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Set set = new HashSet<>();
		
		set.add(22);
		set.add(11);
		set.add(55);
		set.add(33);
		set.add(44);
		set.add(10);
	
		System.out.println(set);
		System.out.println();
		
		TreeSet set2 = new TreeSet<Integer>(set);
		
		System.out.println(set2);
	}

}
