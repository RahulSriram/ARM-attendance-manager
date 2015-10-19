import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.awt.Desktop;
import javax.swing.JFileChooser;
import java.sql.*;

public class project
{
	public static void main(String[] args)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e)
        {
        	e.printStackTrace();
        }
//		student s=new student();
//		String rep;
//		setup.firstRun();
//		setup.makeList();
//		s.init();
//		do
//		{
//			System.out.print("Enter regno: ");
//			rep=util.getString();
//			
//			if(rep.equals("*"))
//				/*session.main()*/;
//			
//			if(rep.equals("exit"))
//				System.exit(0);
//			else
//				s.update(rep);
//		}while(true);
	}
}

class util
{
	static String getServerData(String choice)
	{
		String ip="",user="",password="",starttime="",endtime="",temp="";
		File file=new File("server.cfg");
		
		do
		{
			try(Scanner sc=new Scanner(file))
			{
				while(sc.hasNextLine())
				{
					temp=sc.nextLine();
					if(temp.startsWith("#"))
						;
					if(temp.split("=")[0].equals("Server IP"))
						ip=temp.split("=")[1];
					if(temp.split("=")[0].equals("Username"))
						user=temp.split("=")[1];
					if(temp.split("=")[0].equals("Password"))
						password=temp.split("=")[1];
					if(temp.split("=")[0].equals("Start of Day"))
						starttime=temp.split("=")[1];
					if(temp.split("=")[0].equals("End of Day"))
						endtime=temp.split("=")[1];
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}while(ip.equals(""));
		
		if(choice.equals("IP"))
			return ip;
		if(choice.equals("User"))
			return user;
		if(choice.equals("Password"))
			return password;
		if(choice.equals("StartTime"))
			return starttime;
		if(choice.equals("EndTime"))
			return endtime;
		return null;
	}
	
	static void updateStartOfDay(String time)
	{
		File file=new File("server.cfg");
		String temp=null;
		
		try(Scanner sc=new Scanner(file))
		{
			String t;
			while(sc.hasNextLine())
			{
				t=sc.nextLine();
				if(t.split("=")[0].equals("Start of Day"))
				{
					temp=t.split("=")[1];
					break;
				}
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		if(time.compareTo(temp)<0)
		{
			try(FileWriter fw=new FileWriter(file,true))
			{
				try(Scanner sc=new Scanner(file))
				{
					String a="";
					while(sc.hasNextLine())
					{
						temp=sc.nextLine();
						if(!temp.split("=")[0].equals("Start of Day"))
							a+=temp+"\n";
					}
					a+="Start of Day="+time;
					fw.write(a);
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	static void updateEndOfDay(String time)
	{
		File file=new File("server.cfg");
		String temp=null;
		
		try(Scanner sc=new Scanner(file))
		{
			String t;
			while(sc.hasNextLine())
			{
				t=sc.nextLine();
				if(t.split("=")[0].equals("End of Day"))
				{
					temp=t.split("=")[1];
					break;
				}
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		if(time.compareTo(temp)>0)
		{
			try(FileWriter fw=new FileWriter(file,true))
			{
				try(Scanner sc=new Scanner(file))
				{
					String a="";
					while(sc.hasNextLine())
					{
						temp=sc.nextLine();
						if(!temp.split("=")[0].equals("End of Day"))
							a+=temp+"\n";
					}
					a+="End of Day="+time;
					fw.write(a);
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	static boolean testServerConnection(String ip,String user,String password) throws SQLException
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+ip,user,password))
		{
			return conn!=null;
		}
	}
	
	static String[] listClasses()
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP"),util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("SHOW Databases"))
				{
					String a;
					while (rs.next())
					{
						a=rs.getString("Database");
						if((!a.equals("information_schema"))&&(!a.equals("mysql"))&&(!a.equals("performance_schema")))
    						list.add(rs.getString("Database"));
					}
				}catch(SQLException e)
				{
					e.printStackTrace();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		String[] a=list.toArray(new String[list.size()]);
		
		return a;
	}
	
	static String[] listDays(String name)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("SHOW Tables"))
				{
					String a;
					while (rs.next())
					{
						a=rs.getString("Database");
						if((!a.equals("Namelist"))&&(!a.equals("Timetable")))
    						list.add(rs.getString("Tables_in_"+name));
					}
				}catch(SQLException e)
				{
					e.printStackTrace();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		String[] a=list.toArray(new String[list.size()]);
		
		return a;
	}
	
	static String[] listColumns(String name,String table)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("desc "+table))
				{
					while (rs.next())
					{
    					list.add(rs.getString("Field"));
					}
				}catch(SQLException e)
				{
					e.printStackTrace();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		String[] a=list.toArray(new String[list.size()]);
		
		return a;
	}
	
	static String[] SQLQuery(String name,String table,String column)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("SELECT "+column+" FROM "+table))
				{
					while (rs.next())
					{
    					list.add(rs.getString(column));
					}
				}catch(SQLException e)
				{
					e.printStackTrace();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		String[] a=list.toArray(new String[list.size()]);
		
		return a;
	}
	
	static void SQLUpdate(String update)
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP"),util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				stmt.executeUpdate(update);
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
	}
	
	static void SQLUpdate(String name,String update)
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				stmt.executeUpdate(update);
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
	}
	
	static void SQLUpdateSilent(String name,String update)
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				stmt.executeUpdate(update);
			}catch(SQLException e)
			{
				;
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
	}
	
	static boolean classExists(String name)
	{
		String dbname;
		
    	try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("IP")+"/"+name,util.getServerData("User"),util.getServerData("Password")))
        {
			try(ResultSet rs=conn.getMetaData().getCatalogs())
			{
				dbname=resultSet.getString(1);
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		if(dbname.equals(name))
			return true;
		else
			return false;
	}
	
	static String getDays(int n)
	{
		String a="";
		
		for(int i=1;i<=n;i++)
		{
			if(i==1)
				a+="Monday";
			if(i==2)
				a+=", Tuesday";
			if(i==3)
				a+=", Wednesday";
			if(i==4)
				a+=", Thursday";
			if(i==5)
				a+=", Friday";
			if(i==6)
				a+=", Saturday";
			if(i==7)
				a+=", Sunday";
		}
		
		return a;
	}
	
	static String printDays(int n)
	{
		String a="";
		
		for(int i=1;i<=n;i++)
		{
			if(i==1)
				a+="Monday text";
			if(i==2)
				a+=",Tuesday text";
			if(i==3)
				a+=",Wednesday text";
			if(i==4)
				a+=",Thursday text";
			if(i==5)
				a+=",Friday text";
			if(i==6)
				a+=",Saturday text";
			if(i==7)
				a+=",Sunday text";
		}
		
		return a;
	}
	
	static String inputTime(String temp)
	{
		String h=null,m=null;
		String[] a;
		
		do
		{
			System.out.print("Input the session's "+ temp +" time (in HH:MM FORMAT): ");
			a=util.getString().split(":");
			if(a[0].length()==1)
				h="0"+a[0];
			if(a[0].length()==2)
				h=a[0];
			if(a[1].length()==1)
				m="0"+a[1];
			if(a[1].length()==2)
				m=a[1];
		}while(h==null||m==null);
		return ("'"+h+":"+m+"'");
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
		a=input.nextInt();	
		return a;
	}
	
//	static String getSeparator()
//	{
//		String a=System.getProperty("os.name").toLowerCase();
//		
//		if(a.startsWith("windows"))
//			return "\\";
//			
//		else
//			return "/";
//	}
//	
//	static String getOS()
//	{
//		String a=System.getProperty("os.name").toLowerCase();
//		
//		return a;
//	}
//	
//	static void openFile(File f)
//	{
//		Desktop dt = Desktop.getDesktop();
//		
//		try
//		{
//			dt.open(f);
//		}catch(IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	static String getDate()
	{
		int d,m,y;
		String dd,mm,yy;
		Calendar time=Calendar.getInstance();
		
		d=time.get(Calendar.DAY_OF_MONTH);
		m=time.get(Calendar.MONTH)+1;
		y=time.get(Calendar.YEAR);
		
		if(d<=9)
			dd="0"+Integer.toString(d);
		else
			dd=Integer.toString(d);
		if(m<=9)
			mm="0"+Integer.toString(m);
		else
			mm=Integer.toString(m);
		if(y<=9)
			yy="0"+Integer.toString(y);
		else
			yy=Integer.toString(y);
		
		return (yy+"_"+mm+"_"+dd);
	}
	
	static int getToday()
	{
		int d;
		Calendar time=Calendar.getInstance();
		
		d=time.get(Calendar.DAY_OF_WEEK);
		if(d==1)
			return 7;
			
		else
			return d-1;
	}
	
	static String sysTime()
	{
		int h,m;
		String hh,mm,a;
		Calendar time=Calendar.getInstance();
		
		h=time.get(Calendar.HOUR_OF_DAY);
		m=time.get(Calendar.MINUTE);
		
		if(h<=9)
			hh="0"+Integer.toString(h);
		else
			hh=Integer.toString(h);
		if(m<=9)
			mm="0"+Integer.toString(m);
		else
			mm=Integer.toString(m);
		
		return (hh+":"+mm);
	}
	
	static int noOfDays()
	{
		Calendar c=Calendar.getInstance();
		
		if((c.get(Calendar.MONTH)==3)||(c.get(Calendar.MONTH)==5)||(c.get(Calendar.MONTH)==8)||(c.get(Calendar.MONTH)==10))
			return 30;
			
		else if(c.get(Calendar.MONTH)==1)
			if(c.get(Calendar.YEAR)%4==0)
				return 29;
			else
				return 28;
				
		else
			return 31;
	}
}

class setup
{
	public static void main(String[] args)
	{
		String rep,classname;
		
		do
		{
			System.out.println("1->create a new class\n2->edit timetable for an existing class\n3->edit the namelist for an existing class\n4->remove a class and all it's associated data fully");
			rep=util.getString();
			
			if(rep.equals("1"))
			{
				int i;
				System.out.print("Input the class name: ");
				classname=util.getString();
				if(util.classExists(name))
					System.out.println("The given class name already exists... Try again");
				else
				{
					String regid,sname,contact,temp;
					boolean b;
					int days;
					
					System.out.println("Enter the max number of characters in the ID Number: ");
					i=util.getInt();
					util.SQLUpdate("CREATE DATABASE "+classname);
					util.SQLUpdate(classname,"CREATE TABLE Namelist(IDNo varchar("+i+") primary key,Name text,Contact text,Attended int,Total int)");
					i=0;
					do
					{
						i++;
						System.out.println("\nEnter Student "+ i +"'s details->\n");
						System.out.print("Enter Student's name: ");
						sname=util.getString();
						System.out.print("Enter Student's register ID: ");
						regid=util.getString();
						System.out.print("Enter Student's email ID: ");
						contact=util.getString();
						util.SQLUpdate(classname,"INSERT INTO Namelist(IDNo,Name,Contact) values('"+regid+"','"+sname+"','"+contact+"')");
						System.out.print("Want to add more Students?[y/n]: ");
						rep=util.getString();
					}while(rep.equals("y")||rep.equals("Y"));
					
					
					System.out.println("\n\nNow, create a timetable for this class\n\n");
					System.out.print("How many days a week (starting from Monday) does the class have sessions on?: ");
					days=util.getInt();
					util.SQLUpdate(classname,"CREATE TABLE Timetable(TimeStart text,TimeEnd text,"+util.printDays(days)+")");
					
					i=0;
					do
					{
						i++;
						System.out.println("\nInput the session "+i+"'s timing (IN 24-HRS FORMAT):\n");
						temp=util.inputTime("start");
						rep=temp;
						util.updateStartOfDay(temp);
						temp=util.inputTime("end");
						rep+=","+temp;
						util.updateEndOfDay(temp);
						System.out.println("Input the names of the sessions that happen in the time period ("+rep+") for the days "+util.getDays(days)+" respectively [NO SPECIAL CHARACTERS EXCEPT '$' AND '_']");
						for(i=0;i<days;i++)
						{
							do
							{
								temp=util.getString();
								Pattern p=Pattern.compile("[^a-z0-9 $_]",Pattern.CASE_INSENSITIVE);
								Matcher m=p.matcher(temp);
								b=m.find();
								if(b)
									System.out.println("UNSUPPORTED SPECIAL CHARACTER ADDED!!\nTry again...");
								else
								{
									util.SQLUpdateSilent(classname,"ALTER TABLE Namelist add("+temp+"Attended int,"+temp+"Total int)");
									rep+=",'"+temp+"'";
								}
							}while(b);
						}
						util.SQLUpdate(classname,"INSERT INTO Timetable values("+rep+")");
						
						System.out.print("Add more sessions?[y/n]: ");
						rep=util.getString();
					}while(rep.equals("y")||rep.equals("Y"));
				}
			}
			
			else if(rep.equals("2"))
			{
				String[] list=util.listClasses();
				String temp,t;
								
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					System.out.println(list[i]);
				System.out.print("Enter the class name to edit timetable for: ");
				classname=util.getString();
				if(util.classExists(classname))
				{
					if((util.sysTime()<util.getServerData("StartTime"))||(util.sysTime()>util.getServerData("EndTime")))
					{
						System.out.print("Do you want to edit the timetable of class "+classname+"?[y/n]: ");
						rep=util.getString();
						if(rep.equals("y")||rep.equals("Y"))
						{
							do
							{
								list=util.listColumns(classname,"Timetable");
								System.out.println("Enter which field to edit:");
								System.out.print(list[0]);
								for(int i=1;i<list.length;i++)
									System.out.print(" | "+list[i]);
								rep=util.getString();
								for(int i=1;i<list.length;i++)
									if(rep.equals(list[i]))
									{
										int j;
										list=util.SQLQuery(classname,"Timetable",rep);
										String[] list1=util.SQLQuery(classname,"Timetable","TimeStart");
										System.out.println("SNo   "+rep+"\n");
										for(j=0;j<list.length;j++)
											System.out.println((j+1)+"->"+list[j]);
										System.out.print("Enter the Sno of the row you want to edit: ");
										j=util.getInt()-1;
										if((j>=0)&&(j<list.length))
										{
											System.out.print("Enter the value to replace the "+(j+1)+"th value in "+rep+": ");
											temp=util.getString();
											util.SQLUpdate(classname,"UPDATE "+classname+" SET "+rep+"= '"+temp+"' WHERE TimeStart='"+list1[j]+"'");
											System.out.println("Successfully updated "+rep+" of "+(j+1)+"th session to "+temp);
										}
										
										else
											System.out.println("The chosen row number does not exist. Try again...");
									}
								System.out.print("Want to continue editing fields on "+classname+"'s Timetable?[y/n]: ");
								rep=util.getString();
							}while(rep.equals("y")||rep.equals("Y"));
						}
					
						else
							System.out.println("Timetable not edited");
					}
					
					else
						System.out.println("DO NOT UPDATE THE TIMETABLE DURING WORKING HOURS!!");
				}
				
				else
					System.out.println("Class not found... Try again...");
			}
			
			else if(rep.equals("3"))
			{
				String[] list=util.listClasses();
				String temp,t;
								
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					System.out.println(list[i]);
				System.out.print("Enter the class name to edit namelist for: ");
				classname=util.getString();
				if(util.classExists(classname))
				{
					if((util.sysTime()<util.getServerData("StartTime"))||(util.sysTime()>util.getServerData("EndTime")))
					{
						System.out.print("Do you want to edit the namelist of class "+classname+"?[y/n]: ");
						rep=util.getString();
						if(rep.equals("y")||rep.equals("Y"))
						{
							do
							{
								list=util.listColumns(classname,"Namelist");
								System.out.println("Enter which field to edit:");
								System.out.print(list[0]);
								for(int i=1;i<list.length;i++)
									System.out.print(" | "+list[i]);
								rep=util.getString();
								for(int i=1;i<list.length;i++)
									if(rep.equals(list[i]))
									{
										int j;
										list=util.SQLQuery(classname,"Namelist",rep);
										String[] list1=util.SQLQuery(classname,"Namelist","IDNo");
										System.out.println("SNo   "+rep+"\n");
										for(j=0;j<list.length;j++)
											System.out.println((j+1)+"->"+list[j]);
										System.out.print("Enter the Sno of the row you want to edit: ");
										j=util.getInt()-1;
										if((j>=0)&&(j<list.length))
										{
											System.out.print("Enter the value to replace the "+(j+1)+"th value in "+rep+": ");
											temp=util.getString();
											util.SQLUpdate(classname,"UPDATE "+classname+" SET "+rep+"= '"+temp+"' WHERE IDNo='"+list1[j]+"'");
											System.out.println("Successfully updated "+rep+" of "+(j+1)+"th student to "+temp);
										}
										
										else
											System.out.println("The chosen row number does not exist. Try again...");
									}
								System.out.print("Want to edit more fields on "+classname+"'s Namelist?[y/n]: ");
								rep=util.getString();
							}while(rep.equals("y")||rep.equals("Y"));
						}
					
						else
							System.out.println("Namelist not edited");
					}
					
					else
						System.out.println("DO NOT UPDATE THE NAMELIST DURING WORKING HOURS!!");
				}
				
				else
					System.out.println("Class not found... Try again...");
			}
			
			else if(rep.equals("4"))
			{
				String[] list=util.listClasses();
				
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					System.out.println(list[i]);
				System.out.print("Enter the name of the class to be removed: ");
				classname=util.getString();
				if(util.classExists(classname))
				{
					System.out.print("\n \t\tCAUTION!!\nTHIS ACTION WILL REMOVE ALL DATA ASSOCIATED TO CLASS \""+name+"\" PERMANENTLY [NAMELIST,TIMETABLE,REPORTS]!!!\n\n Do you want to continue?[y/n]: ");
					rep=util.getString();
				}
				
				else
					System.out.println("Invalid class name... Try again...");
				
				if(rep.equals("y")||rep.equals("Y"))
				{
					util.SQLUpdate("DROP DATABASE "+classname);
					System.out.println("Class successfully deleted");
				}
			}
			System.out.print("Return to the class management menu?[y/n]: ");
			rep=util.getString();
		}while(rep.equals("y")||rep.equals("Y"));
	}	
	
	static void firstRun()
	{
		String ip="",user="",password="",rep="";
		boolean login;
		File file=new File("server.cfg");
		
		if(!file.exists())
		{
        	do
        	{
        		System.out.print("Enter the IP Address of the MySQL Server: ");
				ip=util.getString();
				System.out.print("Enter the Username for "+ip+": ");
				user=util.getString();
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
        			System.out.print("Error connecting to the MySQL Server\n\nPossible causes are\n->You have inputted a wrong Username or Password\n->The IP Address of the MySQL server is wrong\n\nIf the IP address and login details were correct, try inputting the IP Address of the server followed by a ':' and then the port number of the MySQL Server(Default port number for MySQL is 3306)\n\n\n Want to try again?[y/n]: ");
        			rep=util.getString();
				}
        	}while(login=false&&(rep.equals("y")||rep.equals("Y")));
        	
        	if(login)
        	{
        		try(FileWriter fw=new FileWriter(file))
        		{
        			fw.write("#You can add your comments to this file by putting a '#' in front of your comment\n#Edit this file to change the configuration settings for the program\n");
        			fw.write("\nServer IP="+ip);
        			fw.write("\nUsername="+user);
        			fw.write("\nPassword="+password);
        		}catch(IOException e)
        		{
        			e.printStackTrace();
        		}
        	}
        }
	}
	
	static void makeList()
	{
		String a="";
		File dir=new File(util.getServerData("IP")+"timetable");
		File[] f=dir.listFiles();
		
		if(f==null||f.length==0)
		{
			System.out.print("RUN THE SETUP JAVA PROGRAM FIRST...\n\nDo you want to launch the program now?[y/n]: ");
			a=util.getString();
			if(a.equals("y")||a.equals("Y"))
			{
				String[] temp=null;
				setup.main(temp);
			}
			
			else
				System.out.println("Please run the setup java application first...");
			System.exit(0);
		}
		
		for(int i=0;i<f.length;i++)
		{
			if(f[i].getName().endsWith(".csv"))
			{
				try(Scanner sc=new Scanner(f[i]))
				{
					sc.nextLine();
					String[] temp=null;
					int x;
					
					for(int j=0;j<util.getToday();j++)
					{
						if(sc.hasNextLine())
						{
							temp=sc.nextLine().split(",");
							a="name,regno";
							for(int k=1;k<temp.length;k++)
								a+=","+temp[k];
						}
					}
					x=temp.length-1;
					dir=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate());
					dir.mkdirs();
					dir=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+f[i].getName());
					if(!dir.exists())
					{
						try(FileWriter fw=new FileWriter(dir))
						{
							fw.write(a);
							dir=new File(util.getServerData("IP")+"namelist"+util.getSeparator()+f[i].getName());
							try(Scanner sc1=new Scanner(dir))
							{
								sc1.nextLine();
								while(sc1.hasNextLine())
								{
									a="\n";
									temp=sc1.nextLine().split(",");
									a+=temp[0]+","+temp[1];
									for(int j=0;j<x;j++)
										a+=",n/a";
									fw.write(a);
								}
							}catch(IOException e)
							{
								e.printStackTrace();
							}
						}catch(IOException e)
						{
							e.printStackTrace();
						}
					}
					
					else
					{
						try(Scanner sc1=new Scanner(dir))
						{
							sc1.nextLine();
							int j;
							while(sc1.hasNextLine())
							{
								temp=sc1.nextLine().split(",");
								a+="\n"+temp[0];
								if(temp.length<=x)
								{
									for(j=1;j<temp.length;j++)
										a+=","+temp[j];
									for(;j<x;j++)
										a+=",n/a";
								}
								
								else
								{
									for(j=1;j<x;j++)
										a+=","+temp[j];
								}
							}
						}catch(IOException e)
						{
							e.printStackTrace();
						}
						
						try(FileWriter fw=new FileWriter(dir))
						{	
							fw.write(a);
						}catch(IOException e)
						{
							e.printStackTrace();
						}
					}
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}

class session
{
	String status;
	String name;
	String start;
	String end;
	session next;
	
	session()
	{
		status="n/a";
		name=null;
		start=null;
		end=null;
		next=null;
	}
	
	void insert(String start,String end)
	{
		session p=this,newnode;
		newnode=new session();
		
		if(newnode!=null)
    	{
    		newnode.start=start;
    		newnode.end=end;
    		while(p.next!=null)
    			p=p.next;
    		p.next=newnode;
    		newnode.next=null;
    	}
    	
    	else
    		System.out.println("error");
	}
	
	void display()
	{
		session p=this;
		String a;
		
		if(!p.status.equals("n/a"))
			a="present";
		else
			a="absent";
		System.out.println("->\nname: "+p.name+"\nstatus: "+a+"\nstart: "+p.start+"\nend: "+p.end);
	}
	
	void displayAll()
	{
		session p=this;
		String a;
		int i=1;
		
		System.out.println("\nSessions:");
		while(p!=null)
		{			
			if(!p.status.equals("n/a"))
				a="present";
			else
				a="absent";
			System.out.println(i+"->\nname: "+p.name+"\nstatus: "+a+"\nstart: "+p.start+"\nend: "+p.end);
			p=p.next;
			i++;
		}
	}
}


class student
{
	static int length;
	String name;
	String regno;
	String batch;
	student next;
	session session;
	
	student find(String regno)
	{
		student p;
		
		do
		{
			p=this.next;
			
			if(regno.equals("exit"))
				System.exit(0);
			
			while(p!=null)
			{
				if(p.regno.equals(regno))
					return p;
				else
					p=p.next;
			}
			System.out.print("Sorry, given regno was not found. Try again...\n Enter regno: ");
			regno=util.getString();
		}while(p==null);
		return null;
	}
	
	void refresh()
	{
		File dir=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate());
		File[] f=dir.listFiles();
		student s=this;
		
		for(int i=0;i<f.length;i++)
		{
			if(f[i].getName().endsWith(".csv"))
			{
				try(Scanner sc=new Scanner(f[i]))
				{
					sc.nextLine();
					while(sc.hasNextLine())
					{
						String[] temp=sc.nextLine().split(",");
						s=s.find(temp[1]);
						session q=s.session;
						
						for(int j=2;j<temp.length;j++)
						{
							q.status=temp[j];
							q=q.next;							
						}
					}
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	void update(String regno)
	{
		this.refresh();
		student s=this.find(regno);
		String a=util.sysTime(),b;
		session p=s.session;
		File f=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+s.batch+".csv"),f1=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+s.batch+"(1).csv");
		
		while(p!=null)
		{
			if(a.compareTo(p.start)>=0&&a.compareTo(p.end)<0)
			{
				p.status=a;
				System.out.println("Dear "+ s.name +",\nAttendance given at "+ a +" within time period of: "+p.start+" to "+p.end+" for session: "+p.name);
				s.upload();
				break;
			}
			
			else
				p=p.next;
		}
		if(p==null)
			System.out.println("Sorry "+s.name+", cannot update attendance at this time...");
	}
	
	void upload()
	{
		String a,b;
		File dir=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+this.batch+".csv"),dir1=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+this.batch+"(1).csv");
		try
		{
			dir1.createNewFile();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		try(FileWriter fw=new FileWriter(dir1))
		{
			try(Scanner sc=new Scanner(dir).useDelimiter(","))
			{
				a=sc.nextLine();
				fw.write(a);
				while(sc.hasNextLine())
				{
					a="\n";
					a+=sc.next()+",";
					b=sc.next();
					if(b.indexOf("\n")!=-1)
						b=b.substring(0,b.indexOf("\n"));
					a+=b;
					if(b.equals(this.regno))
					{
						session p=this.session;
						while(p!=null)
						{
							a+=","+p.status;
							p=p.next;
						}
						sc.nextLine();
					}
					else
						a+=sc.nextLine();
					fw.write(a);
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		dir.delete();
		dir1.renameTo(dir);
	}
	
	void init()
	{
		String a="",b="",c="",x="";
		session p=new session();
		File dir=new File(util.getServerData("IP")+"namelist");
		File[] f=dir.listFiles();
		
		for(int i=0;i<f.length;i++)
		{
			if(f[i].getName().endsWith(".csv"))
			{
				c=f[i].getName().replaceAll(".csv","");
				try(Scanner sc=new Scanner(f[i]).useDelimiter(","))
				{
					while(sc.hasNextLine())
					{
						if(p.end!=null)
							p=new session();
						sc.nextLine();
						if(sc.hasNext())
						{
							a=sc.next();
							b=sc.next();
						}
						
						else
						{
							sc.close();
							break;
						}
						dir=new File(util.getServerData("IP")+"timetable"+util.getSeparator()+f[i].getName());
						
						try(Scanner sc1=new Scanner(dir))
						{
							String[] temp=sc1.nextLine().split(",");			//Get session start and end timings
							
							for(int j=1;j<temp.length;j++)
							{
								String[] t=temp[j].split("-");
								
								if(p.start==null&&p.end==null)
								{
									p.start=t[0];
									p.end=t[1];
								}
								else
									p.insert(t[0],t[1]);
								
								if(x.compareTo(t[1])<0)
									x=t[1];
							}
							
							session q=p;										//Get session names
							
							for(int j=1;j<util.getToday();j++)
							{
								sc1.nextLine();
							}
							
							if(sc1.hasNextLine())
							{
								temp=sc1.nextLine().split(",");
							
								for(int j=1;j<temp.length;j++)
								{
									q.name=temp[j];
									q=q.next;
								}
							}
						}catch(IOException e)
						{
							e.printStackTrace();
						}
						
						dir=new File(util.getServerData("IP")+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+f[i].getName());
						
						if(dir.exists())									//Getting previously stored data for the day, if exists
						{
							try(Scanner sc1=new Scanner(dir))
							{
								sc1.nextLine();
								while(sc1.hasNextLine())
								{
									String[] temp=sc1.nextLine().split(",");
									session q=p;
									
									if(b.equals(temp[1]))
										for(int j=2;j<temp.length;j++)
										{
											q.status=temp[j];
											q=q.next;
										}
								}

							}catch(IOException e)
							{
								e.printStackTrace();
							}
						}
						this.insert(a,b,c,p);
					}
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	void display()
	{
		student p=this;
		
		System.out.println("Student ->\nregno: "+p.regno+"\nname: "+p.name+"\nbatch: "+p.batch);
		p.session.displayAll();
	}
	
	void displayAll()
	{
		student p=this.next;
		int i=1;
		
		System.out.println("\nList:");
		while(p!=null)
		{
			session p1=p.session;
			System.out.println("*******************Student "+i+"->\nregno: "+p.regno+"\nname: "+p.name+"\nbatch: "+p.batch);
			p.session.displayAll();
			p=p.next;
			i++;
		}
	}
	
	void insert(String a,String b,String c,session s)
	{
		student p=this,newnode;
		newnode=new student();
		
		if(newnode!=null)
    	{
    		newnode.name=a;
    		newnode.regno=b;
    		newnode.batch=c;
    		newnode.session=s;
    		while(p.next!=null)
    			p=p.next;
    		p.next=newnode;
    		newnode.next=null;
    		this.length++;
    	}
	}
}
