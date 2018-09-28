package test;

import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayA {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Integer [] AA = new Integer[10];
		
		Arrays.fill(AA, 10);
		
		Arrays.asList(AA).stream().forEach(System.out::println);
		
		Arrays.stream(AA).forEach(System.out::println);
		
		Stream.of(AA).forEach(System.out::println);
		
		System.out.println(Arrays.deepToString(AA));
		
		int [] [] ar = new int [3][4];
		
		for (int [] Row : ar)
			Arrays.fill(Row, 20);
		
		System.out.println(Arrays.deepToString(ar));
		
		System.out.println(ar.length);
		
		Integer integer =0  ;
		
		System.out.println((integer.MIN_VALUE));
		
		
	}

}
