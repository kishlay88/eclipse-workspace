package tree;

public class treeMain {

	public static void main(String... args) {
		
		treeimplement tImp = new treeimplement();
		
		//Tree1
		treeNode Root1 = new treeNode(1);
		treeNode x = new treeNode(2);
		treeNode y = new treeNode(3);
		treeNode xa = new treeNode(4);
		treeNode xb = new treeNode(5);
		treeNode ya = new treeNode(6);
		treeNode yb = new treeNode(7);
		treeNode ybx = new treeNode(8);
		//Tree1
		
		
		Root1.right=y;
		Root1.left=x;
		
		x.left=xa;
		x.right=xb;
		
		y.left=ya;
		y.right=yb;
		
		yb.left=ybx;
		
		System.out.println("PreOrder Tree traversal Tree1");
		tImp.PreOrderTreePrint(Root1);
		System.out.println("InOrder Tree traversal Tree1");
		tImp.InOrderTreePrint(Root1);
		System.out.println("PostOrder Tree traversal Tree1");
		tImp.PostOrderTreePrint(Root1);
		
		System.out.println("Height of the Tree1 = " + tImp.BinaryTreeHeight(Root1));
		System.out.println("Diameter of the Tree1 = " + tImp.treeDiameter(Root1));
		
		
		//Tree2
		
		treeNode Root2 = new treeNode(1);
		treeNode u = new treeNode(2);
		treeNode v = new treeNode(3);
		treeNode ua = new treeNode(4);
		treeNode ub = new treeNode(5);
		treeNode va = new treeNode(6);
		treeNode vb = new treeNode(7);
		treeNode vbu = new treeNode(8);
		
		Root2.right=u;
		Root2.left=v;
		
		u.left=ua;
		u.right=ub;
		
		v.left=va;
		v.right=vb;
		
		//vb.left=vbu;
		
		System.out.println("PreOrder Tree traversal Tree2");
		tImp.PreOrderTreePrint(Root2);
		System.out.println("InOrder Tree traversal Tree2");
		tImp.InOrderTreePrint(Root2);
		System.out.println("PostOrder Tree traversal Tree2");
		tImp.PostOrderTreePrint(Root2);
		
		System.out.println("Height of the Tree2 = " + tImp.BinaryTreeHeight(Root2));
		System.out.println("Diameter of the Tree2 = " + tImp.treeDiameter(Root2));
		
		System.out.println("Tree1 & Tree2 are isomorphic = " + treeimplement.IsoTree(Root1, Root2));
		
	}
}
