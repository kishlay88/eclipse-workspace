package linkedListNew;

public class CircularLLusingSingleLL {

	Node last;
	Node head;

	public void insert(int i) {

		Node node = new Node();
		node.data = i;
		if (last == null) {
			last = node;
			head = node;
		} else {

			last.next = node;
			node.next = head;
			last = node;
		}

	}

	public void displayCircularLL() {

		Node head = last.next;

		System.out.println("CircularLL is = ");
		Node itr = head;
		while (itr != last) {
			System.out.println(itr.data);
			itr = itr.next;
		}
		System.out.println(itr.data);
	}

	public void detectLoop() {

		Node p = head;
		Node q = head;

		boolean flag = false;

		while (p != null && (q != null && p.next != null)) {

			p = p.next;
			q = q.next.next;

			if (p == q) {
				flag = true;
				break;
			} else {
				flag = false;
			}

		}

		Node r = head;

		if (flag == true) {
			while (true) {
				if (r == p) {
					System.out.println("Loop Detected at " + p.data);
					break;
				}

				p = p.next;
				r = r.next;

			}
		}

		else
			System.out.println("No loop Detected");

	}
}
