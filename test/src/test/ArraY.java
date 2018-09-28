package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ArraY {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List LL = new ArrayList<Integer>();
		
		Integer[]  LLI = new Integer[] {4,5,6 };
		
		LL.add(1);
		LL.add(2);
		LL.add(3);
		
		Integer [] aInteger = new Integer[20]; 
		
		//String[] strArray = new String[] {"John", "Mary", "Bob"};
		
		System.out.println(Arrays.toString(LLI));
		System.out.println(LL.toString());
		Arrays.asList(LL).stream().forEach(System.out::println);
		System.out.println(Collections.max(Arrays.asList(LLI)));
		Stream.of(LL).forEach(System.out::println);
		
		Arrays.stream(LLI).forEach(System.out::print);

		LL.stream().forEach(System.out::println);
		
		
	}

}
