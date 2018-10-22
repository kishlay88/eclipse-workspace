package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Example class
 */
public class FilesFolders extends Thread {
	// utility method to print a string
	static String Path = "";
	static void print(String value) {
		System.out.println(value);
	}

	private static ArrayList<String> sortAll(String dirName) {
		File directory = new File(dirName);
		File[] filesArray = directory.listFiles();
		ArrayList<String> aList = new ArrayList<String>();

		Arrays.sort(filesArray);

		for (File file : filesArray) {
			if (file.isDirectory()) {
				aList.add(file.getName());
			}
		}
		return aList;

	}

	/*static public void callsort(String string) {

		System.out.println(sortAll(string));
	}*/

	public void run() {
			
			System.out.println(sortAll(Path));
	}

	public static void main(String[] args) {

		Path = "Y:\\git_repos";
		
		FilesFolders filesFolders = new FilesFolders();
		
		filesFolders.start();
		// System.out.println(sortAll("Y:\\git_repos");

	}
}
