package test;

import java.awt.List;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;

public class removedupArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a[] = {1,2,3,3,4,2,5,1,3,7,8,7,8,99};
		Integer [] integer = ArrayUtils.toObject(a);
		
		LinkedList<Integer> list = new LinkedList<>(Arrays.asList(integer));
		
		list.sort(null);

		int b[] = new int [(list.getLast()>a.length+1)? list.getLast() : a.length+1];
		
		for (int i = 0 ; i < a.length; i++) {
			
			b[a[i]]++;
			
		}
		
		System.out.println(Arrays.toString(b));
		
		for(int k =0 ; k < b.length ; k++) {
			if (b[k] != 0 ) System.out.print(k+ "");
		}
	}

}
