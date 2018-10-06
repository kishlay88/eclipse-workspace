package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.ZipEntry;

public class FileManiP {

	public static void main(String[] args) throws IOException {
		
		File file = new File("Y:\\KSC_SNR1_config_06072018_new_naming.txt");
		Scanner scanner = new Scanner(file);
		//BufferedReader bufferedReader = new BufferedReader(new FileReader(file));


		scanner.useDelimiter("/Z");
			System.out.println(scanner.next());
	}

}
