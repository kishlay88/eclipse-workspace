package test;

class Single {

	private static Single single;

	private Single() throws CloneNotSupportedException{

	}

	public static Single getInstance() throws CloneNotSupportedException  {
		synchronized (Single.class) {

			if (single != null) {
				return single;
			}

			return new Single();
		}
	}

}

public class SingleTonS {

	public static void main(String[] args) throws InterruptedException, CloneNotSupportedException {
		// TODO Auto-generated method stub

		Single single = Single.getInstance();

		System.out.println(single);

	}
}
