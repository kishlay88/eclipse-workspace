package dataStructuresInJava;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


class Student implements Comparable<Student>{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + marks;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + roll;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (age != other.age)
			return false;
		if (marks != other.marks)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (roll != other.roll)
			return false;
		return true;
	}

	String name;
	int age;
	int roll;
	int marks;
	
	public Student(String name , int age , int roll , int marks) {
		// TODO Auto-generated constructor stub
		this.name= name;
		this.age=age;
		this.roll=roll;
		this.marks = marks;
	}
	
	public String toString() {
		
		return String.format("(Name = %s , Age = %d  , Roll No = %d  ,  Marks = %d)\n",this.name,this.age,this.roll,this.marks );
	}
	
	public int compareTo(Student s) {
		// TODO Auto-generated method stub
		return (this.marks-s.marks);
	}
	
}

class Student1 implements Comparator<Student1>{

	String name;
	int age;
	int roll;
	int marks;
	
	public Student1(String name , int age , int roll , int marks) {
		// TODO Auto-generated constructor stub
		this.name= name;
		this.age=age;
		this.roll=roll;
		this.marks = marks;
	}
	
	public Student1() {
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		//return ("Name = " + this.name + "Age = " + this.age + "Roll no = "+ this.roll + "Marks =" + this.marks );
		
		return String.format("(Name = %s , Age = %d  , Roll No = %d  ,  Marks = %d)\n",this.name,this.age,this.roll,this.marks );
		
	}
	
	
	@Override
	public int compare(Student1 o1, Student1 o2) {
		// TODO Auto-generated method stub
		return (o1.marks-o2.marks);
	}
}


public class compare {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Student s[] = new Student[3];
		s[0]= new Student("KK1", 30, 1, 50);
		s[1]= new Student("KK2", 40, 2, 90);
		s[2]= new Student("KK3", 43, 3, 45);
		
		Arrays.sort(s);
		
		System.out.println("Using Comparable Interface" + Arrays.toString(s));
		
		Student1 s1[] = new Student1[3];
		s1[0]= new Student1("KK4", 65, 1, 80);
		s1[1]= new Student1("KK5", 67, 2, 88);
		s1[2]= new Student1("KK6", 68, 3, 43);
		
		
		Arrays.sort(s1,new Student1());
		System.out.println("Using Comparator Interface" + Arrays.toString(s1));
		
		Arrays.sort(s1,Collections.reverseOrder(new Student1()));
		System.out.println("Using Comparator Interface(Reverse)" + Arrays.toString(s1));
	}

}
