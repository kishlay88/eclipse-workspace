package test;

import java.util.Comparator;

public class Student {

	public int roll;
	public String name;
	
	public Student(int r, String n) {
		// TODO Auto-generated constructor stub
		this.roll = r;
		this.name = n;
	
	}
	
	public  String toString()
	{
		return (String.format("%d %s", this.roll , this.name));
	}
	
}

class comp implements Comparator<Student>{
	
	@Override
	public int compare(Student a ,Student b)
	{
		return (a.roll - b.roll);
	}

}