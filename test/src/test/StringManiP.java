package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Removal {
	
	public List<String> remove(String input , int indeX) {
		
		String s1 = input;
		
		ArrayList<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list2 = Arrays.asList(s1.split(""));
		
		list1.addAll(list2);
		
		System.out.println("Before\t: " + list1);
		list1.remove(0);
			
		return list1;
	}
}
public class StringManiP {

	public static void main(String[] args) {
		
		Removal removal = new Removal();
		
		System.out.println("After\t: " + removal.remove("Kishlay",1));
		
		
	}

}
