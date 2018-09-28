package test;

//Java code illustrating destroy() 
//method for windows operating system
public class ProcessDemo {
	public static void main(String[] args) 
	{
		try
		{
			
			// create a new process
			System.out.println("Creating Process");
			
			ProcessBuilder builder = new ProcessBuilder("notepad.exe");
			Process pro = builder.start();
	
			// wait 10 seconds
			System.out.println("Waiting");
			Thread.sleep(10000);
	
			// kill the process
			pro.destroy();
			System.out.println("Process destroyed");
	
		} 
			catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
}
