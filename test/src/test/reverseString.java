package test;

import org.omg.CORBA.PUBLIC_MEMBER;

public class reverseString {

	static StringBuffer sBuffer = new StringBuffer("Kishlay is");

	public static void swap(int i, int j) {

		char s = sBuffer.charAt(j);
		sBuffer.setCharAt(j, sBuffer.charAt(i));
		sBuffer.setCharAt(i, s);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stu
		
		
		int k =0 , start =0 , end = 0;
		while(k< sBuffer.length()-1) {
			while(sBuffer.charAt(k) != ' ' &&  k < sBuffer.length()-1) {
				k++;
				end = k;
				
			}
			
			
			for ( int p = start ; p<end ; p++) {
				reverseString.swap(p, (end-p));
			
			}
			
			System.out.println(sBuffer);
			start= end+2;
			k=start;
		
		}
	
		
	
	}
}