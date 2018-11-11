package dataStructuresInJava;


class BSearch {

	public int BSearchMethodRecursion(int arr[] , int low, int high , int key) {

		if (low==high) {
			if (arr[low]==key) {
				return low;
			}
			else
				return -1;
		}
		else {
			
			int mid = (high + low) / 2;
			
			if (arr[mid] == key) 
				return mid;
			
			if (arr[mid] > key) {
				return BSearchMethodRecursion(arr , low, mid - 1 , key);
			} else {
				return BSearchMethodRecursion(arr,mid + 1, high, key);
			}
		}
		
		}

	public int BSearchMethodIteration(int[] arr,  int key) {
		// TODO Auto-generated method stub
	
		int high = arr.length-1;
		int low = 0;
		while(low <= high)
		{
			int mid = (low+high)/2;
			
			if(arr[mid]==key) {
				return mid;
			}
			
			if(arr[mid]>arr[low])
				high=mid-1;
			else
				low=mid+1;
			
		}
		
		return -1;
	}
		
	}

public class BinarySearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int key = 110;
		int arr[] = {10, 20 , 30, 40, 50 };
		BSearch bSearch = new BSearch();

		int length = arr.length - 1;
		System.out.println("Via Recursion :" + bSearch.BSearchMethodRecursion(arr  , 0, length , key));
		System.out.println("Via Iteration :" + bSearch.BSearchMethodIteration(arr  , key));

	}

}
