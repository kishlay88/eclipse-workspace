package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public class NumWordsLine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String iString = "hi how are you . hi i am kishlay . kishlay is good . hi . hello";
		
		String [] string1 = iString.split(" ");
		List<String> list = new ArrayList<String>();
		
		list=Arrays.asList(string1);
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(String strings : list)
			{
				Integer freq = map.get(strings);
				map.put(strings, (freq==null)? 1 : freq+1);
			}
		
		System.out.println("Distinct Words = " + map.size());
		System.out.println("Map Entries = " + map);
		
		Map<String, Integer> map2 = new TreeMap<String , Integer>(map);
		
		
		System.out.println("The Map to TreeMap converted Map : " + map2);
		
		
		for (Iterator iterator = map.keySet().iterator() ; iterator.hasNext() ; ) {
			 int i = map.get(iterator.next());
			 
			 System.out.println(i);
		}
		
		for (Map.Entry<String, Integer> e : map.entrySet())
		{
		    System.out.println(e.getKey() + "\t:\t" + e.getValue());
			map.put(e.getKey(), (e.getValue().intValue()));
		}
		
		System.out.println(map);
		
		
	}

}
