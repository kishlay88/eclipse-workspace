package test;

public class Employeee implements Comparable<Employeee>{

	public int salary;
	public String Name;
	
	public Employeee(String nString ,int sal) {
		// TODO Auto-generated constructor stub
		this.Name = nString;
		this.salary = sal;
	}
	
	@Override
	public String toString()
	{
		return String.format("(%s , %d)", this.Name , this.salary);
	}
	
	
	@Override
    public int compareTo(Employeee employee) {
        return this.salary - employee.salary;
    }
	
	
}
