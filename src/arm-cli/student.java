package arm;

import java.io.*;
import java.awt.Desktop;

public class student
{
	public static void main(String[] list)
	{
		String classname=null,rep=null;
		boolean available=true;
		
		if(list.length>1)
		{
			available=false;
			
			System.out.println("Choose class to edit");
			for(int i=0;i<list.length;i++)
				System.out.println(list[i]);
			System.out.print("Your choice: ");
			classname=util.getString();
			
			for(int i=0;i<list.length;i++)
				if(classname.equals(list[i]))
				{
					available=true;
					break;
				}
		}
		
		else
			classname=list[0];
		
		if(available)
		{
			do
			{
				System.out.print("1->Edit today's report for class "+classname+"\n2->View reports for a selected day\n3->Print a day's report\nYour choice: ");
				rep=util.getString();
				
				if(rep.equals("1"))
				{
					String[] idno=util.SQLQuery(classname,"SELECT IDNo FROM "+util.getDate());
					System.out.print("Enter ID Number of student to edit their attendance: ");
					String repid=util.getString();
					
					available=false;
					
					for(int i=0;i<idno.length;i++)
						if(repid.equals(idno[i]))
						{
							available=true;
							break;
						}
					
					if(available)
					{
						String[] session=util.SQLQuery(classname,"SELECT SessionName FROM Timetable WHERE Day='"+util.getDay()+"'");
						int repint,i;
						
						System.out.println("Select a session to change attendance for "+repid);
						for(i=0;i<session.length;i++)
							System.out.println((i+1)+" -> "+session[i]);
						System.out.print("Your choice [1-"+i+"]: ");
						repint=util.getInt();
						
						if(repint>=1&&repint<=i)
						{
							String status=util.SQLQuery(classname,"SELECT Status FROM "+util.getDate()+" WHERE Session="+repint+" AND IDNo='"+repid+"'")[0];
							
							if(status==null)
								System.out.println("Student has not yet entered attendance for this session");
							
							else
								System.out.println("Student has entered attendance at "+status+" for this session");
							System.out.print("What would you like to do?\nType 'present' to mark the student as present\nType 'absent' to mark the student as absent\nYour choice: ");
							rep=util.getString().toLowerCase();
							
							if(rep.equals("present"))
							{
								util.SQLUpdate(classname,"UPDATE "+util.getDate()+" SET Status='"+util.sysTime()+"' WHERE Session="+repint+" AND IDNo='"+repid+"'");
								System.out.println("Successfully marked student "+repid+" as present for session "+session[repint-1]);
							}
							
							else if(rep.equals("absent"))
							{
								util.SQLUpdate(classname,"UPDATE "+util.getDate()+" SET Status=null WHERE Session="+repint+" AND IDNo='"+repid+"'");
								System.out.println("Successfully marked student "+repid+" as absent for session "+session[repint-1]);
							}
							
							else
								System.out.println("INVALID CHOICE!!");
						}
					}
				}
				
				else if(rep.equals("2")||rep.equals("3"))
				{
					String repdate="";
					String[] date=util.listReports(classname);
					available=false;
					System.out.println("Enter a date from the following dates:");
					for(int i=0;i<date.length;i++)
					{
						date[i].replace("-","_");
						System.out.println("-> "+date[i]);
					}
					System.out.print("Your choice: ");
					repdate=util.getString();
					
					for(int i=0;i<date.length;i++)
						if(repdate.equals(date[i]))
						{
							available=true;
							break;
						}
					
					if(available)
					{
						repdate.replace("_","-");
						String[] idno=util.SQLQuery(classname,"SELECT DISTINCT IDNo FROM "+repdate),name=util.SQLQuery(classname,"SELECT DISTINCT Name FROM Namelist JOIN "+repdate+" ON Namelist.IDNo="+repdate+".IDNo"),session=util.SQLQuery(classname,"SELECT DISTINCT Session FROM "+repdate),status=util.SQLQuery(classname,"SELECT Status FROM "+repdate);
						
						if(rep.equals("2"))
						{
							int k=0;
							
							for(int i=0;i<idno.length;i++)
							{
								System.out.print(name[i]+" ("+idno[i]+") -> ");
								for(int j=0;j<session.length;j++)
								{
									if(status[k]==null)
										status[k]="null ";
									System.out.print(" "+status[k]+" |");
									k++;
								}
								System.out.println();
							}
						}
						
						else
						{
							File file=new File("temp.csv");
							Desktop desktop=Desktop.getDesktop();
							
							try(FileWriter fw=new FileWriter(file))
							{
								String temp="Name,ID";
								int k=0;
								
								for(int i=0;i<session.length;i++)
									temp+=",Session "+session[i];
								
								for(int i=0;i<idno.length;i++)
								{
									temp+="\n"+name[i]+","+idno[i];
									for(int j=0;j<session.length;j++)
									{
										temp+=","+status[k];
										k++;
									}
								}
								fw.write(temp);
								desktop.print(file);
							}catch(IOException e)
							{
								e.printStackTrace();
							}finally
							{
								file.delete();
							}
						}
					}
					
					else
						System.out.println("Given date does not exist, or is not inputted in correct format... Try again.");
				}
				
				else
					System.out.println("INVALID CHOICE!!");
					
				System.out.print("Continue editing reports for class "+classname+"?[y/n]: ");
				rep=util.getString();
			}while(rep.startsWith("y")||rep.startsWith("Y"));
		}
		
		else
			System.out.println("INVALID CHOICE!!");
	}
	
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
	
	static void update(String idno)
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
						System.out.println("Dear "+name+",\nAttendance given at "+time+" within time period of: "+start[i]+" to "+end[i]+" for session: "+session[i]);
					}
					
					else
						System.out.println("Dear "+name+" you have already been given attendance for session: "+session[i]+" at "+temp);
					
					break;
				}
			
			if(i==session.length)
				System.out.println("Dear "+name+",\nNo sessions are in progress now...");
		}
		
		else
			System.out.println("ID Number was not found to match any student... Try again...");
	}
}
