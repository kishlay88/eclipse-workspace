package dataStructuresInJava;

import java.util.HashMap;
import java.util.Map;

public class ToSumProblem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int a [] = { 1 ,2 ,3,4,5};
		int result[] = new int[2] ;
		int target = 5;
		
		Map<Integer,Integer> map = new HashMap<>();
		for (int i =0 ; i < a.length ; i++) {
			
			if (!map.containsKey(target-a[i])) {
				map.put(a[i] , i);
			}
			else {
				
				result[1]= i;
				result[0]=map.get(target-a[i]);
			}
		}
		
		System.out.println(result[0]+ " " + result[1]);
	}

}
