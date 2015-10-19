package arm;

import java.io.*;
import java.sql.*;

public class setup
{
	public static void main()
	{
		Console console=System.console();
		String idno="",password="";
		boolean staff=false,admin=false;
		String[] stafflist=util.SQLQuery("ARM_config","SELECT IDNo FROM staff"),adminlist=util.SQLQuery("ARM_config","SELECT IDNo FROM admin");
		
		util.clrscr();
		System.out.print("Staff ID: ");
		idno=util.getString();
		System.out.print("Password: ");
		password=new String(console.readPassword());
		
		for(int i=0;i<stafflist.length;i++)
			if(idno.equals(stafflist[i]))
			{
				staff=true;
				break;
			}
		
		if(!staff)
			for(int i=0;i<adminlist.length;i++)
				if(idno.equals(adminlist[i]))
				{
					admin=true;
					break;
				}
		
		if(staff)
		{
			if(password.equals(util.SQLQuery("ARM_config","SELECT Password FROM staff WHERE IDNo='"+idno+"'")[0]))
				student.main(util.SQLQuery("ARM_config","SELECT Class FROM staff WHERE IDNo='"+idno+"'"));
		
			else
				System.out.println("Invalid Username or Password");
		}
		
		else if(admin)
		{
			if(password.equals(util.SQLQuery("ARM_config","SELECT Password FROM admin WHERE IDNo='"+idno+"'")[0]))
				setup.classManagement();
				
			else
				System.out.println("Invalid Username or Password");
		}
		
		else
			System.out.println("Invalid Username or Password");
	}
	
	static void classManagement()
	{
		String rep,classname;
		
		do
		{
			util.clrscr();
			System.out.print("1->create a new class\n2->edit timetable for an existing class\n3->edit the namelist for an existing class\n4->remove a class and all it's associated data fully\n\nYour choice: ");
			rep=util.getString();
			
			if(rep.equals("1"))
			{
				int i=0;
				
				util.clrscr();
				System.out.print("Input the class name: ");
				classname=util.getString();
				
				if(util.classExists(classname))
					System.out.println("The given class name already exists... Try again");
				
				else
				{
					String idno,sname,contact,temp;
					String[] sno=util.SQLQuery("ARM_config","SELECT SNo FROM classes");
					int days;
					boolean b=false;
					
					do
					{
						i++;
						b=false;
						
						for(int j=0;j<sno.length;j++)
							if(Integer.toString(i).equals(sno[j]))
							{
								b=true;
								break;
							}
						
						if(!b)
						{
							util.SQLUpdate("ARM_config","INSERT INTO classes VALUES("+i+",'"+classname+"')");
							System.out.print("Enter the ID Number of the staff incharge of the class: ");
							String user=util.getString();
							System.out.print("Enter the Password for '"+user+"': ");
							Console console=System.console();
							String password=new String(console.readPassword());
							util.SQLUpdate("ARM_config","INSERT INTO staff VALUES('"+user+"','"+classname+"','"+password+"')");
						}
					}while(b);
					
					util.SQLUpdate("CREATE DATABASE "+util.getClass(classname));
					util.SQLUpdate(classname,"CREATE TABLE Namelist(IDNo varchar("+util.getServerData("IDSize")+") PRIMARY KEY,Name text,Contact text,Password text)");
					i=0;
					do
					{
						i++;
						System.out.println("\nEnter Student "+ i +"'s details->\n");
						System.out.print("Enter Student's name: ");
						sname=util.getString();
						System.out.print("Enter Student's ID Number: ");
						idno=util.inputIDNo();
						util.updateIDSize(classname,idno);
						System.out.print("Enter Student's email ID: ");
						contact=util.getString();
						util.SQLUpdate(classname,"INSERT INTO Namelist(IDNo,Name,Contact) VALUES('"+idno+"','"+sname+"','"+contact+"')");
						util.SQLUpdate("ARM_config","INSERT INTO students VALUES('"+idno+"','"+classname+"')");
						System.out.print("Want to add more Students?[y/n]: ");
						rep=util.getString();
					}while(rep.startsWith("y")||rep.startsWith("Y"));
					
					
					System.out.println("\n\nNow, create a timetable for this class\n\n");
					System.out.print("How many days a week (starting from Monday) does the class have sessions on?: ");
					
					do
					{
						days=util.getInt();
						if((days<7)&&(days>1))
							break;
						else
							System.out.print("INVALID INPUT!!! Try again: ");
					}while(true);
					
					util.SQLUpdate(classname,"CREATE TABLE Timetable(Day varchar(9),Session int,TimeStart text,TimeEnd text,SessionName text,PRIMARY KEY(Day,Session))");
					util.SQLUpdate(classname,"CREATE TABLE Percentage(IDNo varchar("+util.getServerData("IDSize")+"),SessionName varchar("+util.getServerData("SessionNameSize")+"),Attended int DEFAULT 0,Total int DEFAULT 0,PRIMARY KEY(IDNo,SessionName))");
						System.out.print("Do you want to use the same timings for all days?[y/n]: ");
					rep=util.getString();
					if(rep.startsWith("y")||rep.startsWith("Y"))
					{
						i=0;
						do
						{
							String start,end;
							
							i++;
							System.out.println("\nInput the session "+i+"'s timing [IN 24-HRS FORMAT]:\n");
							start=util.inputTime("Start");
							util.updateWorkingHours("StartTime",start);
							end=util.inputTime("End");
							util.updateWorkingHours("EndTime",end);
							for(int j=1;j<=days;j++)
								util.SQLUpdate(classname,"INSERT INTO Timetable(Day,Session,TimeStart,TimeEnd) values('"+util.getDay(j)+"','"+i+"','"+start+"','"+end+"')");
							
							System.out.print("Add more sessions?[y/n]: ");
							rep=util.getString();
						}while(rep.startsWith("y")||rep.startsWith("Y"));
						
						
						int n=i;
											
						for(i=1;i<=days;i++)
						{
							temp="";
							System.out.println("Enter the name of the sessions that occur on "+util.getDay(i));
							for(int j=1;j<=n;j++)
							{
								System.out.print("Session "+j+": ");
								rep=util.getString();
								util.SQLUpdate(classname,"UPDATE Timetable SET SessionName='"+rep+"' WHERE Day='"+util.getDay(i)+"' AND Session="+j);
								util.updatePercentage(classname,rep);
							}
						}
					}
					
					else
					{
						for(i=1;i<=days;i++)
						{
							int j=0;
							temp="";
							
							System.out.println("Enter the details of the sessions that occur on "+util.getDay(i));
							do
							{
								j++;
								System.out.print("Name of Session "+j+": ");
								rep=util.getString();
								String start="",end="";
									
								System.out.println("\nEnter the Timings for session "+j+" [IN 24-HRS FORMAT]:\n");
								start=util.inputTime("Start");
								util.updateWorkingHours("StartTime",start);
								end=util.inputTime("End");
								util.updateWorkingHours("EndTime",end);
								util.SQLUpdate(classname,"INSERT INTO Timetable VALUES('"+util.getDay(i)+"','"+j+"','"+start+"','"+end+"','"+rep+"')");
								util.updatePercentage(classname,rep);
								
								System.out.print("Add more sessions for "+util.getDay(i)+"?[y/n]: ");
								rep=util.getString();
							}while(rep.startsWith("y")||rep.startsWith("Y"));
						}
					}						
				}
			}
			
			else if(rep.equals("2"))
			{
				if((util.sysTime().compareTo(util.getServerData("StartTime"))<0)||(util.sysTime().compareTo(util.getServerData("EndTime"))>0))
				{
					String[] list=util.listClasses();
					String temp;
					
					System.out.println("Existing classes:");
					for(int i=0;i<list.length;i++)
						System.out.println(list[i]);
					System.out.print("Enter the class name to edit timetable for: ");
					classname=util.getString();
					
					if(util.classExists(classname))
					{
						System.out.print("Do you want to edit the timetable of class "+classname+"?[y/n]: ");
						rep=util.getString();
						if(rep.startsWith("y")||rep.startsWith("Y"))
						{
							do
							{
								util.clrscr();
								System.out.print("What would you like to do to "+classname+"'s timetable?\n1) Add a new session\n2) Remove a session from a day\n3) Add another day\n4) Change a day's timetable\n5) Remove a day's timetable\n\nYour choice: ");
								rep=util.getString();
								if(rep.equals("1"))
								{
									System.out.print("Would you like to add the session to all days?[y/n]: ");
									rep=util.getString();
									if(rep.startsWith("y")||rep.startsWith("Y"))
									{
										String start,end;
										String[] session=util.SQLQuery(classname,"SELECT MAX(Session) FROM Timetable GROUP BY Day ORDER BY FIELD(Day,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')");
										String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable ORDER BY FIELD(Day,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')");
										System.out.println("\nEnter the session's Start and End times: ");
										start=util.inputTime("Start");
										util.updateWorkingHours("StartTime",start);
										end=util.inputTime("End");
										util.updateWorkingHours("EndTime",end);
										list=new String[day.length];
										System.out.println("\nEnter what sessions happen during "+start+" to "+end+" for "+day.length+" days");
										for(int i=0;i<day.length;i++)
										{
											System.out.print(day[i]+": ");
											list[i]=util.getString();
										}
										
										for(int i=0;i<day.length;i++)
										{
											util.SQLUpdate(classname,"INSERT INTO Timetable VALUES('"+day[i]+"',"+session[i]+"+1,'"+start+"','"+end+"','"+list[i]+"')");
											util.updatePercentage(classname,list[i]);
										}
									}
									
									else
									{
										int i,repint;
										String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable ORDER BY FIELD(Day,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')");
										
										System.out.println("Choose the Day you want to add the session in");
										for(i=0;i<day.length;i++)
											System.out.println((i+1)+"->"+day[i]);
										System.out.print("Your choice [1-"+i+"]: ");
										repint=util.getInt();
										if((repint>=1)&&(repint<=i))
										{
											String[] session=util.SQLQuery(classname,"SELECT SessionName FROM Timetable WHERE Day='"+day[repint-1]+"' ORDER BY Session");
											String[] starttime=util.SQLQuery(classname,"SELECT TimeStart FROM Timetable WHERE Day='"+day[repint-1]+"' ORDER BY Session");
											String[] endtime=util.SQLQuery(classname,"SELECT TimeEnd FROM Timetable WHERE Day='"+day[repint-1]+"' ORDER BY Session");
											String start="",end="";
											int j;
											
											System.out.println("Existing sessions in "+day[repint-1]);
											for(j=0;j<session.length;j++)
												System.out.println("Session "+(j+1)+" -> "+session[j]+" from "+starttime[j]+" to "+endtime[j]);
											System.out.println("\nEnter the session's Start and End times: ");
											start=util.inputTime("Start");
											util.updateWorkingHours("StartTime",start);
											end=util.inputTime("End");
											util.updateWorkingHours("EndTime",end);
											System.out.print("Enter the name of the Session that happens in "+start+" to "+end+": ");
											rep=util.getString();
											util.SQLUpdate(classname,"INSERT INTO Timetable VALUES('"+day[repint-1]+"',"+j+"+1,'"+start+"','"+end+"','"+rep+"')");
											util.updatePercentage(classname,rep);
											System.out.println("Session "+rep+" successfully added to timetable of "+classname);
										}
										
										else
											System.out.println("INVALID CHOICE!!");
									}
								}
								
								else if(rep.equals("2"))
								{
									int i,repint,dayno;
									String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable ORDER BY FIELD(Day,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')");
									
									System.out.println("Choose the Day you want to remove the session from");
									for(i=0;i<day.length;i++)
										System.out.println(i+1+"->"+day[i]);
									System.out.print("Your choice [1-"+i+"]: ");
									dayno=util.getInt();
									if((dayno>=1)&&(dayno<=i))
									{
										String[] session=util.SQLQuery(classname,"SELECT SessionName FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String[] starttime=util.SQLQuery(classname,"SELECT TimeStart FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String[] endtime=util.SQLQuery(classname,"SELECT TimeEnd FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String start="",end="";
										System.out.println("Existing sessions in "+day[dayno-1]);
										for(int j=0;j<session.length;j++)
											System.out.println("Session "+(j+1)+" -> "+session[j]+" from "+starttime[j]+" to "+endtime[j]);
										System.out.print("Enter the serial number of the session to delete: ");
										repint=util.getInt();
										
										if((repint>=1)&&(repint<=session.length))
										{
											util.SQLUpdate(classname,"DELETE FROM Timetable WHERE Day='"+day[dayno-1]+"' AND Session="+repint);
											util.SQLUpdate(classname,"UPDATE Timetable SET Session=Session-1 WHERE Day='"+day[dayno-1]+"' AND Session>"+repint);
											util.removePercentage(classname,session[repint-1]);
											System.out.println("Session "+session[repint-1]+" successfully removed from timetable of "+classname);
										}
																				
										else
											System.out.println("INVALID CHOICE!!");
									}
																			
									else
										System.out.println("INVALID CHOICE!!");
								}
								
								else if(rep.equals("3"))
								{
									int j=0;
									temp="";
									boolean available=true;
									
									System.out.print("Enter the day you want to add a timetable for: ");
									rep=util.getString();
									rep.toLowerCase();
									rep=rep.substring(0,1).toUpperCase()+rep.substring(1);
									
									if(rep.equals("Monday")||rep.equals("Tuesday")||rep.equals("Wednesday")||rep.equals("Thursday")||rep.equals("Friday")||rep.equals("Saturday")||rep.equals("Sunday"))
									{
										String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable");
										for(int i=0;i<day.length;i++)
											if(rep.equals(day[i]))
											{
												available=false;
												break;
											}
									}
									
									else
										available=false;
									
									if(available)
									{
										System.out.println("Enter the details of the sessions that occur on "+rep);
										String day=rep;
										
										do
										{
											j++;
											System.out.print("Name of Session "+j+": ");
											temp=util.getString();
											String start="",end="";
												
											System.out.println("\nEnter the Timings for session "+j+" [IN 24-HRS FORMAT]:\n");
											start=util.inputTime("Start");
											util.updateWorkingHours("StartTime",start);
											end=util.inputTime("End");
											util.updateWorkingHours("EndTime",end);
											util.SQLUpdate(classname,"INSERT INTO Timetable VALUES('"+day+"','"+j+"','"+start+"','"+end+"','"+temp+"')");
											util.updatePercentage(classname,temp);
											
											System.out.print("Add more sessions for "+day+"?[y/n]: ");
											rep=util.getString();
										}while(rep.startsWith("y")||rep.startsWith("Y"));
									}
									
									else
										System.out.println("There exists a timetable for "+rep+" already (or) you have entered an invalid name for a day");
								}
								
								else if(rep.equals("4"))
								{
									String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable ORDER BY FIELD(Day,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')");
									int i,dayno,repint;
									boolean available=false;
									
									System.out.println("Choose a day from the following to edit timetable");
									for(i=0;i<day.length;i++)
										System.out.println("SNo "+(i+1)+" -> "+day[i]);
									System.out.print("Your choice [1-"+i+"]: ");
									dayno=util.getInt();
									if((dayno>=1)&&(dayno<=i))
									{
										String[] session=util.SQLQuery(classname,"SELECT SessionName FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String[] starttime=util.SQLQuery(classname,"SELECT TimeStart FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String[] endtime=util.SQLQuery(classname,"SELECT TimeEnd FROM Timetable WHERE Day='"+day[dayno-1]+"' ORDER BY Session");
										String start="",end="";
										System.out.println("Choose a session to edit its details\n\nExisting sessions in "+day[dayno-1]);
										for(int j=0;j<session.length;j++)
											System.out.println("Session "+(j+1)+" -> "+session[j]+" from "+starttime[j]+" to "+endtime[j]);
										System.out.print("Your choice[Enter Session number]: ");
										repint=util.getInt();
										
										for(int j=0;j<session.length;j++)
										{
											if(repint-1==j)
											{
												available=true;
												break;
											}
										}
										
										if(available)
										{
											do
											{
												System.out.println("What would you like to do?\n->Enter 1 for changing the start time of session "+repint+"\n->Enter 2 for changing the end time of session "+repint+"\n->Enter 3 for changing the name of session"+repint+"\n Your choice: ");
												int j=util.getInt();
												
												if(j==1)
												{
													System.out.println("Enter the new start time for the session "+repint);
													rep=util.inputTime("Start");
													util.updateWorkingHours("StartTime",rep);
													util.SQLUpdate(classname,"UPDATE Timetable SET TimeStart='"+rep+"' WHERE Day='"+day[dayno-1]+"' AND Session="+repint);
													System.out.println("Successfully updated start time of session "+repint+" to "+rep);
												}
												
												else if(j==2)
												{
													System.out.println("Enter the new end time for the session "+repint);
													rep=util.inputTime("End");
													util.updateWorkingHours("EndTime",rep);
													util.SQLUpdate(classname,"UPDATE Timetable SET TimeEnd='"+rep+"' WHERE Day='"+day[dayno-1]+"' AND Session="+repint);
													System.out.println("Successfully updated end time of session "+repint+" to "+rep);
												}
												
												else if(j==3)
												{
													System.out.print("Enter the new name for the session "+repint+": ");
													rep=util.getString();
													util.SQLUpdate(classname,"UPDATE Timetable SET SessionName='"+rep+"' WHERE Day='"+day[dayno-1]+"' AND Session="+repint);
													util.updatePercentage(classname,rep);
													util.removePercentage(classname,session[repint-1]);
													System.out.println("Successfully updated name of session "+repint+" to "+rep);
												}
												
												else
													System.out.println("INVALID CHOICE!!");
												
												System.out.print("Continue to edit properties of session "+repint+" in "+day[dayno-1]+"?[y/n]: ");
												rep=util.getString();
											}while(rep.startsWith("y")||rep.startsWith("Y"));
										}
										
										else
											System.out.println("INVALID CHOICE!!");
									}
									
									else
										System.out.println("INVALID CHOICE!!");
								}
								
								else if(rep.equals("5"))
								{
									boolean available=false;
									
									System.out.print("Enter the day you want to remove timetable from: ");
									rep=util.getString();
									rep.toLowerCase();
									rep=rep.substring(0,1).toUpperCase()+rep.substring(1);
									
									if(rep.equals("Monday")||rep.equals("Tuesday")||rep.equals("Wednesday")||rep.equals("Thursday")||rep.equals("Friday")||rep.equals("Saturday")||rep.equals("Sunday"))
									{
										String[] day=util.SQLQuery(classname,"SELECT DISTINCT Day FROM Timetable");
										for(int i=0;i<day.length;i++)
											if(rep.equals(day[i]))
											{
												available=true;
												break;
											}
									}
									
									if(available)
									{
										System.out.print("Are you sure you want to remove "+rep+"'s Timetable[y/n]: ");
										temp=util.getString();
										if(temp.startsWith("y")||temp.startsWith("Y"))
										{
											String[] session=util.SQLQuery(classname,"SELECT DISTINCT SessionName FROM Timetable WHERE Day='"+rep+"'");
											util.SQLUpdate(classname,"DELETE FROM Timetable WHERE Day='"+rep+"'");
											for(int i=0;i<session.length;i++)
												util.removePercentage(classname,session[i]);
											System.out.println(rep+"'s Timetable was removed successfully");
										}
										else
											System.out.println(rep+"'s Timetable was not removed");
									}
									
									else
										System.out.println("No timetable found for "+rep);
								}
								
								else
									System.out.println("INVALID CHOICE!!");
								
								System.out.print("\nWant to continue editing fields on "+classname+"'s Timetable?[y/n]: ");
								rep=util.getString();
							}while(rep.startsWith("y")||rep.startsWith("Y"));
						}
					
						else
							System.out.println("Timetable not edited");
					}
					
					else
						System.out.println("Class not found... Try again...");
				}
				
				else
					System.out.println("DO NOT UPDATE THE TIMETABLE DURING WORKING HOURS!!");
			}
			
			else if(rep.equals("3"))
			{				
				if((util.sysTime().compareTo(util.getServerData("StartTime"))<0)||(util.sysTime().compareTo(util.getServerData("EndTime"))>0))
				{
					String[] list=util.listClasses();
					String temp;
					System.out.println("Existing classes:");
					for(int i=0;i<list.length;i++)
						System.out.println(list[i]);
					System.out.print("Enter the class name to edit namelist for: ");
					classname=util.getString();
					
					if(util.classExists(classname))
					{
						System.out.print("Do you want to edit the namelist of class "+classname+"?[y/n]: ");
						rep=util.getString();
						if(rep.startsWith("y")||rep.startsWith("Y"))
						{
							do
							{
								util.clrscr();
								System.out.print("What would you like to do?\n1) Add a new student\n2) Edit a student's details\n3) Remove a student\n\nYour choice: ");
								rep=util.getString();
								System.out.println("Existing students");
								String[] name=util.SQLQuery(classname,"SELECT Name FROM Namelist");
								String[] idno=util.SQLQuery(classname,"SELECT IDNo FROM Namelist");
								for(int i=0;i<idno.length;i++)
									System.out.println(idno[i]+" -> "+name[i]);
								
								if(rep.equals("1"))
								{
									String repid,repname,repcontact;
									boolean available=true;
									System.out.print("Enter the IDNo of the new student: ");
									repid=util.getString();
									for(int i=0;i<idno.length;i++)
										if(repid.equals(idno[i]))
										{
											available=false;
											break;
										}
									if(available)
									{
										String[] session=util.SQLQuery(classname,"SELECT DISTINCT SessionName FROM Percentage");
										
										util.updateIDSize(classname,repid);
										System.out.print("Enter the name of the new student: ");
										repname=util.getString();
										System.out.print("Enter the email ID of the new student: ");
										repcontact=util.getString();
										util.SQLUpdate(classname,"INSERT INTO Namelist(IDNo,Name,Contact) VALUES('"+repid+"','"+repname+"','"+repcontact+"')");
										for(int i=0;i<session.length;i++)
											util.SQLUpdate(classname,"INSERT INTO Percentage(IDNo,SessionName) VALUES('"+repid+"','"+session[i]+"')");
										util.SQLUpdate("ARM_config","INSERT INTO students VALUES('"+repid+"','"+classname+"')");
										System.out.println("Successfully added student '"+repid+"' to class "+classname);
									}
									
									else
										System.out.println("This ID Number has already been used by another student...");
								}
								
								else if(rep.equals("2"))
								{
									String repid;
									int tempint=0;
									boolean available=false;
									
									System.out.print("Enter the ID Number of the student: ");
									repid=util.getString();
									for(int i=0;i<idno.length;i++)
										if(repid.equals(idno[i]))
										{
											available=true;
											tempint=i;
											break;
										}
									
									if(available)
									{
										System.out.print("What would you like to do?\n1)Change the name of the student\n2)Change the email ID of the student\n3)Change the ID Number of the student\n\nYour choice: ");
										rep=util.getString();
										
										if(rep.equals("1"))
										{
											System.out.print("Current name: "+name[tempint]+"\nEnter new name: ");
											rep=util.getString();
											util.SQLUpdate(classname,"UPDATE Namelist SET Name='"+rep+"' WHERE IDNo='"+repid+"'");
											System.out.println("Successfully changed the name for "+repid+" from "+name[tempint]+" to "+rep);
										}
										
										else if(rep.equals("2"))
										{
											String[] contact=util.SQLQuery(classname,"SELECT Contact FROM Namelist");
											
											System.out.print("Current email ID: "+contact[tempint]+"\nEnter new email ID: ");
											rep=util.getString();
											util.SQLUpdate(classname,"UPDATE Namelist SET Contact='"+rep+"' WHERE IDNo='"+repid+"'");
											System.out.println("Successfully changed the email ID for "+repid+" from "+contact[tempint]+" to "+rep);
										}
										
										else if(rep.equals("3"))
										{
											System.out.print("Current ID Number: "+idno[tempint]+"\nEnter new ID Number: ");
											rep=util.inputIDNo();
											util.updateIDSize(classname,rep);
											util.SQLUpdate(classname,"UPDATE Namelist SET IDNo='"+rep+"' WHERE IDNo='"+repid+"'");
											util.SQLUpdate(classname,"UPDATE Percentage SET IDNo='"+rep+"' WHERE IDNo='"+repid+"'");
											util.SQLUpdate("ARM_config","UPDATE students SET IDNo='"+rep+"' WHERE IDNo='"+repid+"'");
											System.out.println("Successfully changed the ID Number from "+idno[tempint]+" to "+rep);
										}
										
										else
											System.out.println("INVALID CHOICE!!");
									}
									
									else
										System.out.println("A student with this ID Number does not exist in class "+classname);
								}
								
								else if(rep.equals("3"))
								{
									String repid;
									boolean available=false;
									System.out.print("Enter the IDNo of the student to be removed: ");
									repid=util.getString();
									for(int i=0;i<idno.length;i++)
										if(repid.equals(idno[i]))
										{
											available=true;
											break;
										}
									
									if(available)
									{
										System.out.print("Are you sure you want to delete student '"+repid+"'?[y/n]: ");
										rep=util.getString();
										if(rep.startsWith("y")||rep.startsWith("Y"))
										{
											util.SQLUpdate(classname,"DELETE FROM Namelist WHERE IDNo='"+repid+"'");
											util.SQLUpdate(classname,"DELETE FROM Percentage WHERE IDNo='"+repid+"'");
											util.SQLUpdate("ARM_config","DELETE FROM students WHERE IDNo='"+repid+"'");
										}
										System.out.println("Successfully removed student '"+repid+"' from class "+classname);
									}
									
									else
										System.out.println("Student with ID Number "+repid+" was not found...");
								}
								
								else
									System.out.println("INVALID CHOICE!!");
								
								System.out.print("Want to continue editing fields on "+classname+"'s Namelist?[y/n]: ");
								rep=util.getString();
							}while(rep.startsWith("y")||rep.startsWith("Y"));
						}
					
						else
							System.out.println("Namelist not edited");
					}
					
					else

						System.out.println("Class not found... Try again...");
				}
				
				else
					System.out.println("DO NOT UPDATE THE NAMELIST DURING WORKING HOURS!!");
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
					System.out.print("\n \t\tCAUTION!!\nTHIS ACTION WILL REMOVE ALL DATA ASSOCIATED TO CLASS \""+classname+"\" PERMANENTLY [NAMELIST,TIMETABLE,REPORTS]!!!\n\n Do you want to continue?[y/n]: ");
					rep=util.getString();
				}
				
				else
					System.out.println("Invalid class name... Try again...");
				
				if(rep.startsWith("y")||rep.startsWith("Y"))
				{
					util.SQLUpdate("DROP DATABASE "+util.getClass(classname));
					util.SQLUpdate("ARM_config","DELETE FROM classes WHERE Name='"+classname+"'");
					util.SQLUpdate("ARM_config","DELETE FROM students WHERE Class='"+classname+"'");
					util.SQLUpdate("ARM_config","DELETE FROM staff WHERE Class='"+classname+"'");
					System.out.println("Class successfully deleted");
				}
			}
			System.out.print("Continue using the class management menu?[y/n]: ");
			rep=util.getString();
		}while(rep.startsWith("y")||rep.startsWith("Y"));
		
		System.exit(0);
	}
	
	static void firstRun()
	{
		File file=new File("server.cfg");
		
		if(!file.exists())
		{
			String ip="",user="",password="",rep="y";
			boolean login=false;
			
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
        			System.out.print("Error connecting to the MySQL Server\n\nPossible causes are\n->You are not connected to the network\n->The Username or Password is wrong\n->The Server may be down\n->The IP Address of the MySQL server is wrong\n\nIf the IP address and login details were correct, try inputting the IP Address of the server followed by a ':' and then the port number of the MySQL Server(Default port number for MySQL is 3306)\n\n\n Want to try again?[y/n]: ");
        			rep=util.getString();
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
        		System.out.print("Error connecting to MySQL Server!!! Please check your network connection...\n\nDo you want to Re-enter the server's login details?[y/n]: ");
        		String rep=util.getString();
        		
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
	
	static void createReports()
	{
		String temp="";
		String[] list=util.listClasses();
		
		if(list.length==0)
		{
			System.out.print("RUN THE SETUP JAVA PROGRAM FIRST...\n\nDo you want to launch the program now?[y/n]: ");
			temp=util.getString();
			if(temp.startsWith("y")||temp.startsWith("Y"))
			{
				if(!util.tableExists("ARM_config","admin"))
				{
					System.out.println("Please create a list of admins now");
					String rep="",idno="",password="";
					
					do
					{
						System.out.print("\n\nEnter ID Number: ");
						idno=util.getString();
						System.out.print("Enter a Password for '"+idno+"': ");
						Console console=System.console();
						password=new String(console.readPassword());
						util.SQLUpdate("ARM_config","INSERT INTO admin VALUES('"+idno+"','"+password+"')");
						System.out.print("Want to add more admins?[y/n]: ");
						rep=util.getString();
					}while(rep.startsWith("y")||rep.startsWith("Y"));
				}
				setup.classManagement();
			}
			
			else
				System.out.println("Please run the setup java application first...");
			System.exit(0);
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
		}
	}
	
	static void importMySQL()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e)
        {
        	e.printStackTrace();
        }
	}
}
