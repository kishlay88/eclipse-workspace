package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Anagram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean flag = true;
		String string1="army";
		String string2="mary";
		
		if (string1.equals(string2)) {
			System.out.println("Same String");
			return;
		}
		
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		
		list1 = Arrays.asList(string1.split(""));
		list2 = Arrays.asList(string2.split(""));
		
		Map<String,Integer> map1 = new HashMap<String,Integer>();
		Map<String,Integer> map2 = new HashMap<String,Integer>();
		
		Iterator<String> iterator1 = list1.iterator() ;
		Iterator<String> iterator2 = list2.iterator() ;
		
		map1 = MappingElem.genMap(string1 , list1);
		map2 = MappingElem.genMap(string2, list2);
		
		while(iterator1.hasNext())
		{
			String string12 = iterator1.next().toString();
			Integer I1 = map1.get(string12);
		
			map1.put(string12, (I1==null)? 1 : (I1+1));
		}
	
		
		while(iterator2.hasNext())
		{
			String string12 = iterator2.next().toString();
			Integer I1 = map2.get(string12);
		
			map2.put(string12, (I1==null)? 1 : (I1+1));
		}
		
		if (map1.equals(map2))
		{
			System.out.println("Both Strings are Anagram");
			return;
		}
		else
			System.out.println("Both Strings are Not Anagram");
	}
}

