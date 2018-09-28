package test;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Collection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List list1 = new LinkedList();
		
		list1.add("KK3");	list1.add("KK1"); 	list1.add("KK2");
		
		List list2 = new LinkedList(list1);
		
		System.out.println("Print Original Lists :--");
		System.out.println("List1 -->");
		System.out.println(list1);
		System.out.println("List2 -->");
		System.out.println(list2);

		System.out.println("Print with Iterator");
		
		Iterator I = list2.iterator();
		
		while(I.hasNext()) 
			System.out.print(I.next() + ",");
		
		System.out.println("\nPrint Sublist");
		
		System.out.println(list1.subList(1, 3));
		
	}
}
