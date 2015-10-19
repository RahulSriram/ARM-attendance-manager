package arm;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class setup
{
	static void firstRun()
	{
		File file=new File("server.cfg");
		
		if(!file.exists())
		{
			String ip="",user="",password="",rep="y";
			boolean login=false;
			
        	do
        	{
        		System.out.println("\t\t~~ ARM - The Attendance Manager ~~\n\n");
        		System.out.print("Enter the IP Address of the MySQL Server: ");
				ip=getString();
				System.out.print("Enter the Username for "+ip+": ");
				user=getString();
				System.out.print("Enter the Password for "+ip+": ");
				Console console=System.console();
				password=new String(console.readPassword());
				
				try
				{
					login=util.testServerConnection(ip,user,password);
					if(login)
						System.out.println("Login attempt to MySQL Server was successful");
				}catch(SQLException e)
        		{
        			System.out.print("Error connecting to the MySQL Server\n\nPossible causes are\n->You are not connected to the network\n->The Username or Password is wrong\n->The Server may be down\n->The IP Address of the MySQL server is wrong\n\nIf the IP address and login details were correct, try inputting the IP Address of the server followed by a ':' and then the port number of the MySQL Server(Default port number for MySQL is 3306)\n\n\n Want to try again?[y/n]: ");
        			rep=getString();
				}
				
				if(!rep.startsWith("y")&&!rep.startsWith("Y"))
				{
					System.exit(0);
				}
        	}while(!login);
        	
        	if(login)
        	{        		
        		try(FileWriter fw=new FileWriter(file))
        		{
        			fw.write("#You can add your comments to this file by putting a '#' in front of your comment\n#Always add your comments in a new line, or it may break something\n#Edit this file to change the configuration settings for the MySQL Server\n");
        			fw.write("\nServer IP="+ip);
        			fw.write("\nUsername="+user);
        			fw.write("\nPassword="+password);
        		}catch(IOException e)
        		{
        			e.printStackTrace();
        		}
        		
        		if(!util.classExists("ARM_config"))
        		{
        			util.SQLUpdate("CREATE DATABASE ARM_config");
        			util.SQLUpdate("ARM_config","CREATE TABLE stats(IDSize int,StartTime text,EndTime text,SessionNameSize int)");
        			util.SQLUpdate("ARM_config","INSERT INTO stats VALUES(1,null,null,1)");
        			util.SQLUpdate("ARM_config","CREATE TABLE classes(SNo int PRIMARY KEY AUTO_INCREMENT,Name text)");
        			util.SQLUpdate("ARM_config","CREATE TABLE students(IDNo varchar("+util.getServerData("IDSize")+") PRIMARY KEY,Class text)");
        			util.SQLUpdate("ARM_config","CREATE TABLE staff(IDNo text,Class text,Password text)");
        			util.SQLUpdate("ARM_config","CREATE TABLE admin(IDNo text,Password text)");
        		}
        	}
        }
        
        else
        {
        	try
        	{
        		if(util.testServerConnection(util.getServerData("Server IP"),util.getServerData("Username"),util.getServerData("Password")))
        		{
        			if(!util.classExists("ARM_config"))
        			{
        				util.SQLUpdate("CREATE DATABASE ARM_config");
        				util.SQLUpdate("ARM_config","CREATE TABLE stats(IDSize int,StartTime text,EndTime text,SessionNameSize int)");
        				util.SQLUpdate("ARM_config","INSERT INTO stats VALUES(1,null,null,1)");
        				util.SQLUpdate("ARM_config","CREATE TABLE classes(SNo int PRIMARY KEY,Name text)");
     	    			util.SQLUpdate("ARM_config","CREATE TABLE students(IDNo varchar("+util.getServerData("IDSize")+") PRIMARY KEY,Class text)");
     	    			util.SQLUpdate("ARM_config","CREATE TABLE staff(IDNo text,Class text,Password text)");
        				util.SQLUpdate("ARM_config","CREATE TABLE admin(IDNo text,Password text)");
        			}
        		}
        	}catch(SQLException e)
        	{
        		System.out.println("\t\t~~ ARM - The Attendance Manager ~~\n\n");
        		System.out.print("Error connecting to MySQL Server!!! Please check your network connection...\n\nDo you want to Re-enter the server's login details?[y/n]: ");
        		String rep=getString();
        		
        		if(rep.startsWith("y")||rep.startsWith("Y"))
        		{
        			file.delete();
        			setup.firstRun();
        		}
        		
        		else
        		{
        			System.out.println("Please check your connection to the MySQL Server and try opening the application again");
        			System.exit(0);
        		}
        	}
        }
	}
	
	static int createReports()
	{
		String temp="";
		String[] list=util.listClasses();
		
		if(list.length==0)
		{
			System.out.println("\t\t~~ ARM - The Attendance Manager ~~\n\n");
			System.out.print("It seems this is the first time you are opening this application...\nYou must Setup the application before using it\n\nDo you want to launch the Setup Program now?[y/n]: ");
			temp=getString();
			if(temp.startsWith("y")||temp.startsWith("Y"))
			{
				if((!util.tableExists("ARM_config","admin"))||(util.SQLQuery("ARM_config","SELECT IDNo FROM admin").length==0))
				{
					System.out.println("Please create a list of admins now");
					String rep="",idno="",password="";
					
					do
					{
						System.out.print("\n\nEnter Username: ");
						idno=getString();
						System.out.print("Enter a Password for '"+idno+"': ");
						Console console=System.console();
						password=new String(console.readPassword());
						util.SQLUpdate("ARM_config","INSERT INTO admin VALUES('"+idno+"','"+password+"')");
						System.out.print("Want to add more admins?[y/n]: ");
						rep=getString();
					}while(rep.startsWith("y")||rep.startsWith("Y"));
				}
			}
			
			else
			{
				System.out.println("Please run the setup program first...");
				System.exit(0);
			}
			
			adminMenuUI.main(null);
			return 1;
		}
		
		else
		{
			for(int i=0;i<list.length;i++)
				if(!util.tableExists(list[i],util.getDate()))
				{
					String[] day=util.SQLQuery(list[i],"SELECT DISTINCT Day FROM Timetable");
					boolean dayexists=false;
					
					for(int j=0;j<day.length;j++)
						if(day[j].equals(util.getDay()))
						{
							dayexists=true;
							break;
						}
					
					if(dayexists)
					{
						String[] idno=util.SQLQuery(list[i],"SELECT IDNo FROM Namelist");
						String[] session=util.SQLQuery(list[i],"SELECT SessionName FROM Timetable WHERE Day='"+util.getDay()+"'");
						
						for(int j=0;j<session.length;j++)
							util.SQLUpdate(list[i],"UPDATE Percentage SET Total=Total+1 WHERE SessionName='"+session[j]+"'");
						util.SQLUpdate(list[i],"CREATE TABLE "+util.getDate()+"(IDNo varchar("+util.getServerData("IDSize")+"),Session int,Status text,PRIMARY KEY(IDNo,Session))");
						for(int j=0;j<idno.length;j++)
							for(int k=0;k<session.length;k++)
								util.SQLUpdate(list[i],"INSERT INTO "+util.getDate()+"(IDNo,Session) VALUES('"+idno[j]+"',"+(k+1)+")");
					}
				}
			return 0;
		}
	}
	
	static String getString()
	{
		String a;
		
		Scanner input=new Scanner(System.in);
		a=input.nextLine();
		
		return a;
	}
	
	static int getInt()
	{
		int a;
		Scanner input=new Scanner(System.in);
		
		while(!input.hasNextInt())
		{
			input.next();
			System.out.print("INVALID INPUT!!! Try again: ");
		}
		a=input.nextInt();
		
		return a;
	}
}
