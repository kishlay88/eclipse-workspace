package test;

import java.util.Arrays;
import java.util.Collections;

public class StuComp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Student [] students = new Student[4];		
		
		students[0] = new Student(111 , "a");
		students[1] = new Student(21 , "b");
		students[2] = new Student(300 , "c");
		students[3] = new Student(4 , "d");
		
		System.out.println("Before sorting by roll = " + Arrays.toString(students));
		Arrays.sort(students ,new comp());
		System.out.println("After sorting by roll =" + Arrays.toString(students));
		System.out.println();
	}

}
