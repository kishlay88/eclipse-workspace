package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class removeDupListusingMap {

	
	public static void main(String... args) {
		
		int a[] = {1,2,3,3,4,2,5,1,3,7,8,7,8,9};
			
		Integer l[] = ArrayUtils.toObject(a);
		
		LinkedList<Integer> list = new LinkedList<Integer>(Arrays.asList(l));
		
		Map<Integer, Integer> hMap = new HashMap<>();
		
		Iterator<Integer> iterator = list.iterator();
		
		while(iterator.hasNext()) {
			
			Integer integer = iterator.next();
			hMap.put(integer, (hMap.get(integer)==null) ? 1 : hMap.get(integer)+1);
		}
		
		System.out.println(hMap.keySet());
		list.sort(null);
		System.out.println(list);
	}
}