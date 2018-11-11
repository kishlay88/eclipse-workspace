package linkedListNew;

import java.util.Stack;

import org.w3c.dom.NamedNodeMap;

public class StackImp {

	stack top;

	public void push(int i) {

		stack stack = new stack();
		stack.data = i;
		stack.prev = top;
		top=stack;
		
	}
	
	public int pop() {
	
		int d =0 ;
		if (top==null) {
			System.out.println("Stack Empty !!!");
			System.exit(1);
		}
		else {
			d= top.data;
			top=top.prev;
			
		}
		return d;
	}

	public void displStack() {

		stack node = top;

		while (node != null) {
			System.out.println(node.data);
			node = node.prev;
		}
	}
	
}
