package test;

import java.util.FormatFlagsConversionMismatchException;

public class Palindrome {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean flag = true;
		String string="madam"	;
		
		char [] cs = string.toCharArray() ;
		
		int length = cs.length;
		
		if ((length%2) == 0)
		{
			System.out.println("Not a Palindrome");
			return;
		}
		
		
		for(int k =((length/2)-1); k>=0 ; k--){
			
			if(cs[k]!=cs[length-k-1]) {
				flag = false;
				System.out.println("Not a Palindrome");
				return;
			}
		}
		
		System.out.println("String is Palindrome");
		
		
		
	}

}