package dataStructuresInJava;

import java.util.Arrays;

class Sorts {

	public int[] MergeSortkk(int arr[], int low, int rear) {

		if (low < rear) {

			int mid = (low + rear) / 2;
			MergeSortkk(arr, mid + 1, rear);
			MergeSortkk(arr, low, mid);
			merge(arr, low, rear);
		}
		return arr;

	}

	public void merge(int arr[], int low, int rear) {

		int mid = (low + rear) / 2;
		int left[] = new int[mid];
		int right[] = new int[rear - mid];

		int r = 0;
		for (int p = 0; p < mid; p++) {
			left[p] = arr[r];
			r++;
		}

		for (int q = 0; q < rear - mid; q++) {
			right[q] = arr[r];
			r++;
		}

		int i = 0;
		int j = 0;
		int k = 0;

		while (i < left.length && j < right.length) {

			if (left[i] < right[j]) {
				arr[k] = left[i];
				i++;
				k++;
			} else {
				arr[k] = right[j];
				j++;
				k++;
			}

		}

		for (; i < left.length; i++) {
			arr[k] = left[i];
			k++;
		}

		for (; j < right.length; j++) {
			arr[k] = right[j];
			k++;
		}

		//return arr;

	}

}

 public class Sort1{
	 
	 public static void main(String[] args) {
		 
		 int arr[] = { 1, 20, 10, 40, 100, 5, 65, 76 };
		 
		 Sorts sort = new Sorts();
		 
		 System.out.println("Original Array" + Arrays.toString(arr));
		 System.out.println("Merge Sort : " + Arrays.toString(sort.MergeSortkk(arr, 0, arr.length-1)));
		 
		
	}
 }