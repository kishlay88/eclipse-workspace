package test;

import java.awt.print.Printable;

class student {
	
	public int id;
	public String name;

	public student(int i , String s)
	{
		this.id = i;
		this.name = s;
	
	}
	
	public student() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object obj)
	{
		student s = new student();
		
		s = (student)obj;
		return(s.equals(obj));
		
	}

}

public class EqualsHashcode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		student student1 = new student(1 , "KK1");
		student student2 = new student(2 , "KK2");
		
		if (student1.equals(student2)) 
		{
			System.out.println("Both are equal");
		}
	}
	
}

