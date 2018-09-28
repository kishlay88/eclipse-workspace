package test;

import java.text.Format;
import java.util.Arrays;
import java.util.Collections;

public class EmP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employeee [] employees = new Employeee[4];
		
		employees[0] = new Employeee("a" , 10000);
		employees[1] = new Employeee("b" , 30000);
		employees[2] = new Employeee("c" , 20000);
		employees[3] = new Employeee("d" , 90000);

		System.out.println("Before Sorting" + Arrays.toString(employees));
		
		Arrays.sort(employees,Collections.reverseOrder());
		
		System.out.println("After Sorting" + Arrays.toString(employees));
		
		int [] is =  { 1,2,3,4,5 };
		
		try {
			
			for (int i : is)
				System.out.println(i);
			
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			System.out.println("Array out of Bounds");
		}
		
		
	}

}
