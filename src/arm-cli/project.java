package arm;

public class project
{
	public static void main(String[] args)
	{
		String rep;
		setup.importMySQL();
		setup.firstRun();
		setup.createReports();
		
		do
		{
			util.clrscr();
			System.out.println("\t\t~~ ARM - The Attendance Report Manager v2.0 ~~\n\nEnter '*' for admin panel\nType 'exit' to exit program\n");
			System.out.print("\nEnter ID Number: ");
			rep=util.getString();
			
			if(rep.equals("*"))
				setup.main();
			
			else if(rep.toLowerCase().equals("exit"))
				System.exit(0);
			
			else
				student.update(rep);
		}while(true);
	}
}
