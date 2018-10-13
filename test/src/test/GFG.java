package test;

//Java code to illustrate order of 
//execution of constructors, static 
//and initialization blocks 
class Super {
	static int i =0 ;
    static
	{
    	System.out.println("Static Super Print");
		System.out.println("Static int Print i=" + i);
	}
    
    int k = 5;
    
    {
    	System.out.println("Instance Block Print Super");
    	System.out.println("Value of K=" + k);
    }
    
    
    
    public Super() {

    	System.out.println("i am super constructer");
	}
}

class One {
    static { System.out.println("Static Class One Print"); }
}

class Two extends Super {
    static { System.out.println("Static Class Two Print "); }
    
    {
    	System.out.println("Instance block print in Class Two");
    }
    
    void prnt() {
    	System.out.println(i);
    }
    
    public Two() {
		// TODO Auto-generated constructor stub
    	System.out.println("I am Class Two Constructer");
	}
}

public class GFG {
	
	static int a = 10;
	
    public static void main(String[] args) {
    	
       One o = null;
       //Two t = new Two();
       int k = Super.i;
       
       //t.prnt();
       //System.out.println("Int A=" + a);
        //System.out.println((Object)o == (Object)t);
    }
}


