package test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface MappingElem {
	
	static Map<String, Integer> genMap(String string , List<String> list ){
		
	Iterator<String> iterator = list.iterator() ;
	Map<String,Integer> map = new HashMap<String,Integer>();
			
	while(iterator.hasNext())
	{
		String string12 = iterator.next().toString();
		Integer I1 = map.get(string12);
	
		map.put(string12, (I1==null)? 1 : (I1+1));
	}
	return map;
	}
}
