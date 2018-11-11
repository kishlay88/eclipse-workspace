package dataStructuresInJava;

import java.util.Arrays;

class SortImplementation {

	public int[] SelectionSort(int arr[]) {

		for (int i = 0; i < arr.length; i++) {
			int Lindx = i;

			for (int j = i; j < arr.length; j++) {
				if (arr[j] < arr[i])
					Lindx = j;
			}

			int temp = arr[i];
			arr[i] = arr[Lindx];
			arr[Lindx] = temp;
		}
		return arr;

	}

	public int [] SelectionSortkk(int arr[]) {

		for (int i = 0; i < arr.length; i++) {

			int min = i;
			for (int j = i; j < arr.length; j++) {

				if (arr[j] < arr[min]) {

					min = j;
				}

			}

			int temp = arr[i];
			arr[i] = arr[min];
			arr[min] = temp;

		}
		return arr;

	}

	public int[] BubbleSort(int arr[]) {

		for (int i = 1; i <= arr.length - 1; i++) {

			for (int j = 0; j < arr.length - 1; j++) {

				if (arr[j] > arr[j + 1]) {
					int temp = arr[j + 1];
					arr[j + 1] = arr[j];
					arr[j] = temp;
				}
			}
		}
		return arr;
	}
	
	public int[] BubbleSortkk(int arr[]) {

		for (int i = 0; i < arr.length - 1; i++) {

			for (int j = 0; j < arr.length - 1; j++) {

				if (arr[j] > arr[j + 1]) {
					int temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;

				}

			}

		}

		return arr;
	}

	public int[] InsertionSort(int[] arr) {

		for (int i = 1; i <= arr.length - 1; i++) {
			int min = arr[i];

			for (int j = i - 1; j >= 0; j--) {

				if (arr[j] > min) {
					arr[j] = min;
					arr[i] = arr[j];
				}
			}
		}
		return arr;
	}

	public int[] InsertionSortkk(int arr[]) {

		for (int i = 0; i < arr.length; i++) {

			int pivot = i;
			for (int j = i - 1; j >= 0; j--) {

				if (arr[pivot] < arr[j]) {

					int temp = arr[pivot];
					arr[pivot] = arr[j];
					arr[j] = temp;
					pivot = j;
				}

			}
		}

		return arr;

	}

	public int[] mergeSort(int arr[], int low, int rear) {

		if (low < rear) {

			int mid = (low + rear) / 2;
			mergeSort(arr, mid + 1, rear);
			mergeSort(arr, low, mid);
			merge(arr, low, rear);
		}
		return arr;

	}

	public int[] merge(int arr[], int low, int rear) {

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

		return arr;

	}

	public int[] QuickSort(int arr[], int low, int high) {

		if (low < high) {

			int j = partition1(arr, low, high);
			QuickSort(arr, low, j);
			QuickSort(arr, j + 1, high);
		}

		return arr;

	}

	public int partition1(int arr[], int low, int high) {

		int i = low, j = high;

		int pivot = arr[low];
		while (i < j) {

			do {
				i++;
			} while (arr[i] <= pivot);

			do {
				j--;
			} while (arr[j] > pivot);

			if (i < j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}

		}

		int temp = arr[j];
		arr[j] = arr[low];
		arr[low] = temp;

		return j;
	}

	public long partition(int arr[], int low, int high) {

		int pivot = arr[high];

		int i = low;
		int j = i;
		while (j < high - 1) {

			if (arr[j] <= pivot) {
				i++;
				int temp = arr[j];
				arr[j] = arr[i];
				arr[j] = temp;
			}

			j++;
		}

		int temp = arr[i + 1];
		arr[i + 1] = arr[high];
		arr[high] = temp;

		return (long) i;
	}

}

public class Sort {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int arr[] = { 1, 20, 10, 40, 100, 5, 65, 76 };

		SortImplementation sImp = new SortImplementation();

		System.out.println("Original Array : " + Arrays.toString(arr));
		System.out.println("Sort Using Selection Sort : " + Arrays.toString(sImp.SelectionSort(arr)));
		System.out.println("Sort Using Selection Sort-KK : " + Arrays.toString(sImp.SelectionSortkk(arr)));
		System.out.println("Sort Using Bubble Sort : " + Arrays.toString(sImp.BubbleSort(arr)));
		System.out.println("Sort Using Bubble Sort-KK : " + Arrays.toString(sImp.BubbleSortkk(arr)));
		System.out.println("Sort Using Merge Sort : " + Arrays.toString(sImp.mergeSort(arr, 0, arr.length - 1)));
		System.out.println("Sort Using Quick Sort : " + Arrays.toString(sImp.QuickSort(arr, 0, arr.length - 1)));
		System.out.println("Sort Using Insertion Sort : " + Arrays.toString(sImp.InsertionSort(arr)));
		System.out.println("Sort Using Insertion Sort-KK : " + Arrays.toString(sImp.InsertionSortkk(arr)));
		
		

	}

}
