package arm;

import java.util.*;
import java.text.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.sql.*;

public class util
{
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
		String[] classlist=util.listClasses();
		int temp=Integer.parseInt(util.getServerData("SessionNameSize"));
		
		if(session.length()>temp)
		{
			temp=session.length();
			for(int i=1;i<=classlist.length;i++)
				util.SQLUpdate("Class_"+i,"ALTER TABLE Percentage MODIFY SessionName varchar("+temp+")");
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
			idsize=idno.length();
			util.SQLUpdate("ARM_config","UPDATE stats SET IDSize="+idsize);
		}
		
		util.SQLUpdate(classname,"ALTER TABLE Namelist MODIFY IDNo varchar("+idsize+")");
		util.SQLUpdate(classname,"ALTER TABLE Percentage MODIFY IDNo varchar("+idsize+")");
		if(util.tableExists(classname,util.getDate()))
			util.SQLUpdate(classname,"ALTER TABLE "+util.getDate()+" MODIFY IDNo varchar("+idsize+")");
		util.SQLUpdate("ARM_config","ALTER TABLE students MODIFY IDNo varchar("+idsize+")");
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
						int i;
    					for(i=0;i<temp.length;i++)
    						if(temp[i].equals("AS"))
    							break;
    					
    					while (rs.next())
						{
							if(i<temp.length)
    							list.add(rs.getString(temp[i+1]));
    						
    						else
    						{
    							if(temp[1].equals("DISTINCT"))
    								list.add(rs.getString(temp[2]));
    							
    							else
    								list.add(rs.getString(temp[1]));
    						}
    					}
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
	
	static String getClass(String classname)
	{
		if(classname.equals("ARM_config"))
			return classname;
		
		String sno=util.SQLQuery("ARM_config","SELECT SNo FROM classes WHERE Name='"+classname+"'")[0];
		return ("Class_"+sno);
	}
	
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
	
	static String getDay(String date)
	{
		SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);
		Calendar calendar=new GregorianCalendar();
		java.util.Date theDate=null;
		
		try {
    		theDate=format.parse(date);
		}catch(ParseException e) {
			e.printStackTrace();
		}
    	
    	calendar.setTime(theDate);
		int i=calendar.get(Calendar.DAY_OF_WEEK);
		
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
	
	static int dayNumber(String day)
    {
        if(day.equals("Monday"))
    		return 0;
		if(day.equals("Tuesday"))
        	return 1;
        if(day.equals("Wednesday"))
        	return 2;
        if(day.equals("Thursday"))
        	return 3;
        if(day.equals("Friday"))
        	return 4;
        if(day.equals("Saturday"))
        	return 5;
        if(day.equals("Sunday"))
        	return 6;
        return -1;
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

	static void setLookAndFeel()
	{
		String a=System.getProperty("os.name").toLowerCase();

		if(a.startsWith("windows"))
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		else
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	}
}
