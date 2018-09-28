package test;

import java.awt.print.Printable;

class student {

	public int id;
	public String name;

	public student(int i, String s) {
		this.id = i;
		this.name = s;

	}

	public student() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		student other = (student) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}

public class EqualsHashcode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		student student1 = new student(1, "KK1");
		student student2 = new student(2, "KK2");

		if (student1.equals(student2)) {
			System.out.println("Both are equal");
		}
	}

}
