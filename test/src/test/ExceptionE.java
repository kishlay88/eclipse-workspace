package test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.omg.CORBA.FREE_MEM;

class EE{
	
	private int a;
	
	public EE(String s)
	{
		this.a = Integer.parseInt(s);
	}
	
	public int  getA() {
		return this.a;
	}
}

class MyException extends Exception
{
	MyException(){};
	
	MyException(String str){
		super(str);
	}
}


public class ExceptionE {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Runtime gfg = Runtime.getRuntime();
		long memory1, memory2;
        Integer integer[] = new Integer[1000];
 
        // checking the total memeory
        System.out.println("Total memory is: "
                           + gfg.freeMemory());
        System.gc();
        
        System.out.println("Total memory is: "
                + gfg.freeMemory());
      
	}	

}
