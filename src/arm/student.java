package arm;

import java.io.*;
import java.awt.Desktop;

public class student
{
    static String find(String idno)
	{
		String[] list=util.SQLQuery("ARM_config","SELECT IDNo FROM students");
		
		for(int i=0;i<list.length;i++)
			if(idno.equals(list[i]))
			{
				String classname=util.SQLQuery("ARM_config","SELECT Class FROM students WHERE IDNo='"+idno+"'")[0];
				return classname;
			}
		
		return null;
	}
	
	static String update(String idno)
	{
		String classname=student.find(idno),time=util.sysTime();
		
		if(classname!=null)
		{
			String[] start=util.SQLQuery(classname,"SELECT TimeStart FROM Timetable WHERE Day='"+util.getDay()+"'");
			String[] end=util.SQLQuery(classname,"SELECT TimeEnd FROM Timetable WHERE Day='"+util.getDay()+"'");
			String[] session=util.SQLQuery(classname,"SELECT SessionName FROM Timetable WHERE Day='"+util.getDay()+"'");
			String name=util.SQLQuery(classname,"SELECT Name FROM Namelist WHERE IDNo='"+idno+"'")[0];
			int i=0;
			
			for(i=0;i<session.length;i++)
				if(time.compareTo(start[i])>=0&&time.compareTo(end[i])<0)
				{
					String temp=util.SQLQuery(classname,"SELECT Status FROM "+util.getDate()+" WHERE IDNo='"+idno+"' AND Session="+(i+1))[0];
					
					if(temp==null)
					{
						util.SQLUpdate(classname,"UPDATE "+util.getDate()+" SET Status='"+util.sysTime()+"' WHERE IDNo='"+idno+"' AND Session="+(i+1));
						util.SQLUpdate(classname,"UPDATE Percentage SET Attended=Attended+1 WHERE IDNo='"+idno+"' AND SessionName='"+session[i]+"'");
						return idno+", Attendance given for "+session[i];
					}
					
					else
						return idno+", You've already entered attendance for "+session[i];
				}
			
			if(i==session.length)
				return "No Sessions in progress now!!!";
		}
		
		return "ID Number not found!!!";
	}
}
