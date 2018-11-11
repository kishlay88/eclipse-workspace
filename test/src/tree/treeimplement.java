package tree;

public class treeimplement {

	public void PreOrderTreePrint(treeNode r) {

		if (r == null)
			return;

		System.out.println(r.data);

		PreOrderTreePrint(r.left);
		PreOrderTreePrint(r.right);

	}

	public void PostOrderTreePrint(treeNode r) {

		if (r == null)
			return;

		PostOrderTreePrint(r.left);
		PostOrderTreePrint(r.right);
		System.out.println(r.data);

	}

	public void InOrderTreePrint(treeNode r) {

		if (r == null)
			return;


		InOrderTreePrint(r.left);
		System.out.println(r.data);
		InOrderTreePrint(r.right);

	}
	
	public int BinaryTreeHeight(treeNode r) {
		
		int leftHeight = 0;
		int rightHieght=0;
		if (r==null) return 0;
		
		leftHeight= BinaryTreeHeight(r.left);
		rightHieght= BinaryTreeHeight(r.right);
		
		return java.lang.Math.max(leftHeight, rightHieght)+1;
	}
	
	public int treeDiameter(treeNode r) {
		
		int leftHeight=0 , rightHeight=0 , leftDiam=0 , rightDiam=0;
		if (r == null) return 0; 
		
		leftHeight = BinaryTreeHeight(r.left);
		rightHeight = BinaryTreeHeight(r.right);
		
		leftDiam = treeDiameter(r.left);
		rightDiam = treeDiameter(r.right);
		
		return (java.lang.Math.max((leftHeight + rightHeight+1) ,java.lang.Math.max(leftDiam, rightDiam))); 
		
	}
	
	public static boolean IsoTree(treeNode r1 , treeNode r2) {
		
		if (r1==null && r2==null) return true;
		
		if (r1==null || r2==null) return false;
		
		if (	(IsoTree(r1.left, r2.left) && IsoTree(r1.right, r2.right)) ||
				(IsoTree(r1.left ,r2.right) && IsoTree(r1.right, r2.left))  )
		{
			return true;
		}
		
		return false;
	}

}
