package arm;

import java.util.Calendar;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.sql.*;

public class util
{
	static void clrscr()
	{
		for(int i=0;i<50;i++)
			System.out.println();
	}
	
	static String getServerData(String choice)
	{
		String temp="";
		File file=new File("server.cfg");
		
		if(choice.equals("Server IP")||choice.equals("Username")||choice.equals("Password"))
		{
			try(Scanner sc=new Scanner(file))
			{
				while(sc.hasNextLine())
				{
					temp=sc.nextLine();
					if(temp.startsWith("#"))
						;
					if(temp.split("=")[0].equals(choice))
					{
						temp=temp.split("=")[1];
						break;
					}
				}
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return temp;
		}
		
		else if(choice.equals("IDSize")||choice.equals("StartTime")||choice.equals("EndTime")||choice.equals("SessionNameSize"))
			return util.SQLQuery("ARM_config","SELECT "+choice+" FROM stats")[0];
		
		else
			return null;
	}
	
	static void updateWorkingHours(String choice,String time)
	{
		String temp=util.getServerData(choice);
		
		if(temp==null)
			util.SQLUpdate("ARM_config","UPDATE stats SET "+choice+"='"+time+"'");
		
		else if(choice.equals("StartTime"))
			if(time.compareTo(temp)<0)
				util.SQLUpdate("ARM_config","UPDATE stats SET StartTime='"+time+"'");
		
		else if(choice.equals("EndTime"))
			if(time.compareTo(temp)>0)
				util.SQLUpdate("ARM_config","UPDATE stats SET EndTime='"+time+"'");
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
		String[] list=util.SQLQuery("ARM_config","SELECT Name FROM classes");
		
		return list;
	}
	
	static String[] listReports(String name)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("SHOW TABLES"))
				{
					String table;
					while (rs.next())
					{
						table=rs.getString("Tables_in_"+util.getClass(name));
						if((!table.equals("Namelist"))&&(!table.equals("Timetable"))&&(!table.equals("Percentage")))
    						list.add(table);
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
		
		return list.toArray(new String[list.size()]);
	}
	
	static String[] listColumns(String name,String table)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery("DESC "+table))
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
		
		return list.toArray(new String[list.size()]);
	}
	
	static void updatePercentage(String classname,String session)
	{
		String[] idno=util.SQLQuery(classname,"SELECT IDNo FROM Namelist");
		int temp=Integer.parseInt(util.getServerData("SessionNameSize"));
		
		if(session.length()>temp)
		{
			temp=session.length();
			util.SQLUpdate(classname,"ALTER TABLE Percentage MODIFY SessionName varchar("+temp+")");
			util.SQLUpdate("ARM_config","UPDATE stats SET SessionNameSize="+temp);
		}
		
		for(int i=0;i<idno.length;i++)
			util.SQLUpdateSilent(classname,"INSERT INTO Percentage(IDNo,SessionName) VALUES('"+idno[i]+"','"+session+"')");
	}
	
	static void removePercentage(String classname,String name)
	{
		String[] session=util.SQLQuery(classname,"SELECT DISTINCT SessionName FROM Timetable");
		boolean present=false;
		
		for(int i=0;i<session.length;i++)
			if(name.equals(session[i]))
			{
				present=true;
				break;
			}
		
		if(!present)
			util.SQLUpdate(classname,"DELETE FROM Percentage WHERE SessionName='"+name+"'");
	}
	
	static void updateIDSize(String classname,String idno)
	{
		int idsize=Integer.parseInt(util.getServerData("IDSize"));
		
		if(idno.length()>idsize)
		{
			util.SQLUpdate("ARM_config","UPDATE stats SET IDSize="+idno.length());
			util.SQLUpdate(classname,"ALTER TABLE Namelist MODIFY IDNo varchar("+idno.length()+")");
			util.SQLUpdate("ARM_config","ALTER TABLE students MODIFY IDNo varchar("+idno.length()+")");
		}
	}
	
	static String[] SQLQuery(String name,String query)
	{
		List<String> list=new ArrayList<String>();
		
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
        {
			try(Statement stmt=conn.createStatement())
			{
				try(ResultSet rs=stmt.executeQuery(query))
				{
					String[] temp=query.split(" ");
					
					if(temp[1].equals("*"))
					{
						String table="";
						
						for(int i=0;i<temp.length;i++)
							if(temp[i].equals("FROM"))
							{
								table=temp[i+1];
								break;
							}
						
						String[] column=util.listColumns(name,table);
						while (rs.next())
						{
							for(int i=0;i<column.length;i++)
    							list.add(rs.getString(column[i]));
						}
					}
					
					else
					{
						if(temp[1].equals("DISTINCT"))
							while (rs.next())
    							list.add(rs.getString(temp[2]));
    					
    					else
    						while (rs.next())
    							list.add(rs.getString(temp[1]));
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
		
		return list.toArray(new String[list.size()]);
	}
	
	static void SQLUpdate(String update)
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP"),util.getServerData("Username"),util.getServerData("Password")))
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
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
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
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
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
		if(name.equals("ARM_config"))
		{
    		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP"),util.getServerData("Username"),util.getServerData("Password")))
        	{
				try(Statement stmt=conn.createStatement())
				{
					try(ResultSet rs=stmt.executeQuery("SHOW DATABASES"))
					{
						while(rs.next())
							if(rs.getString("Database").equals(name))
								return true;
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
		}
		
		else
		{
			String[] classname=util.SQLQuery("ARM_config","SELECT Name FROM classes");
			
			for(int i=0;i<classname.length;i++)
				if(name.equals(classname[i]))
					return true;	
		}
		
		return false;
	}
	
	static boolean tableExists(String name,String table)
	{
		try(Connection conn=DriverManager.getConnection("jdbc:mysql://"+util.getServerData("Server IP")+"/"+util.getClass(name),util.getServerData("Username"),util.getServerData("Password")))
        {
			try(ResultSet rs=conn.getMetaData().getTables(null,null,table,null))
			{
				if(rs.next())
					return true;
				
				else
					return false;
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}catch(SQLException e)
		{
    		e.printStackTrace();
		}
		
		return false;
	}
	
	static String inputIDNo()
	{
		String[] idno=util.SQLQuery("ARM_config","SELECT IDNo FROM students");
		String rep="";
		boolean used=false;
		
		do
		{
			rep=util.getString();
			used=false;
			
			for(int i=0;i<idno.length;i++)
				if(idno[i].equals(rep))
				{
					used=true;
					break;
				}
			
			if(used)
			{
				String temp=rep;
				String classname=util.SQLQuery("ARM_config","SELECT Class FROM students WHERE IDNo='"+temp+"'")[0];
				String name=util.SQLQuery(classname,"SELECT Name FROM students WHERE IDNo='"+temp+"'")[0];
				
				System.out.print("ID Number "+rep+" is already used by "+name+" of class "+classname+"\nDo you want to REMOVE "+name+"'s entry?\n\nCAREFUL! THIS WILL REMOVE THE STUDENT "+name+" OF "+classname+"\nContinue?[y/n]: ");
				rep=util.getString();
				if(rep.startsWith("y")||rep.startsWith("Y"))
				{
					util.SQLUpdate(classname,"DELETE FROM Namelist WHERE IDNo='"+temp+"'");
					util.SQLUpdate(classname,"DELETE FROM Percentage WHERE IDNo='"+temp+"'");
					util.SQLUpdate("ARM_config","DELETE FROM students WHERE IDNo='"+temp+"'");
					System.out.println(temp+" is now assigned to the current student...");
					return temp;
				}
				
				else
					System.out.print("Enter another new ID Number: ");
			}
		}while(used);
		
		return rep;
	}
	
	static String getClass(String classname)
	{
		if(classname.equals("ARM_config"))
			return classname;
		
		String sno=util.SQLQuery("ARM_config","SELECT SNo FROM classes WHERE Name='"+classname+"'")[0];
		return ("Class_"+sno);
	}
	
	static String inputTime(String temp)
	{
		String rep=null,hh=null,mm=null;
		String[] a=null,match={":","-","."};
		
		do
		{
			System.out.print("Input the session's "+ temp +" time [PUT ':' or '.' or '-' BETWEEN HOURS AND MINUTES]: ");
			rep=util.getString();
			
			for(int i=0;i<match.length;i++)
				if(rep.contains(match[i]))
				{
					a=rep.split(match[i]);
					if((Integer.parseInt(a[0])>23)||(Integer.parseInt(a[1])>59)||(!a[0].matches("\\d+"))||(!a[1].matches("\\d+")))
						break;
					
					if(a[0].length()==1)
						hh="0"+a[0];
					if(a[0].length()==2)
						hh=a[0];
					if(a[1].length()==1)
						mm="0"+a[1];
					if(a[1].length()==2)
						mm=a[1];
					
					return (hh+":"+mm);
				}
			
			System.out.println("INVALID INPUT!!! TRY AGAIN...");
		}while(true);
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
//	
//	static int noOfDays()
//	{
//		Calendar c=Calendar.getInstance();
//		
//		if((c.get(Calendar.MONTH)==3)||(c.get(Calendar.MONTH)==5)||(c.get(Calendar.MONTH)==8)||(c.get(Calendar.MONTH)==10))
//			return 30;
//			
//		else if(c.get(Calendar.MONTH)==1)
//			if(c.get(Calendar.YEAR)%4==0)
//				return 29;
//			else
//				return 28;
//				
//		else
//			return 31;
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
	
	static String getDay()
	{
		Calendar time=Calendar.getInstance();
		int i=time.get(Calendar.DAY_OF_WEEK);
		
		if(i==1)
			return "Sunday";
		if(i==2)
			return "Monday";
		if(i==3)
			return "Tuesday";
		if(i==4)
			return "Wednesday";
		if(i==5)
			return "Thursday";
		if(i==6)
			return "Friday";
		if(i==7)
			return "Saturday";
		return null;
	}
	
	static String getDay(int i)
	{
		if(i==1)
			return "Monday";
		if(i==2)
			return "Tuesday";
		if(i==3)
			return "Wednesday";
		if(i==4)
			return "Thursday";
		if(i==5)
			return "Friday";
		if(i==6)
			return "Saturday";
		if(i==7)
			return "Sunday";
		return null;
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
}
