package CLinkedList;

import java.awt.HeadlessException;
import java.security.acl.LastOwnerException;

import CLinkedList.CLinkedList.Node;

public class CLinkListMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

			CLinkedList cLinkedList = new CLinkedList();
			
			cLinkedList.append(1);
			cLinkedList.append(2);
			cLinkedList.append(3);
			cLinkedList.append(4);
			cLinkedList.append(5);
			cLinkedList.append(6);
			
			//CLinkedList.Node n = cLinkedList.new Node(7);
			
			System.out.println("Loop detected !!! No. of Nodes in loop = " + cLinkedList.detectLoop());
	}

}
