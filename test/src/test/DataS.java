package test;

import java.util.*;
import java.util.Map.Entry;

public class DataS {


	static DataS s = new DataS();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		System.out.println(s.toString());
		
		
		// Create a LinkedHashMap 
        LinkedHashMap<String,Integer> m =  
                new LinkedHashMap<String, Integer>(); 
          
        m.put("1 - Bedroom" , 25000); 
        m.put("2 - Bedroom" , 50000); 
        m.put("3 - Bedroom" , 75000); 
        m.put("1 - Bedroom - hall", 65000); 
        m.put("2 - Bedroom - hall", 85000); 
        m.put("3 - Bedroom - hall", 105000); 
        
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        
        ListIterator iterator = list.listIterator();
        
        for(; iterator.hasNext() ;)
        {
        	System.out.println(iterator.next());
       
		}
        
        Set set = new HashSet<>();
        
        set.add("a");
        set.add(1);
        set.add("`");
        
        Iterator iterator2 = set.iterator();
        
        for(; iterator2.hasNext();)
        	System.out.println(iterator2.next());
        
        Map<Integer,String> map = new HashMap<Integer,String>();
        
        map.put(1,"a");
        map.put(2, "b");
        
        System.out.println(map.values());
        
        Set<Map.Entry<Integer, String>> set2 = map.entrySet();
        
        for(Map.Entry<Integer, String> entry : set2)
        {
        	System.out.println("1st=" + entry.getKey() + "\n2nd=" + entry.getValue());
        }
        
	}
			
	}
