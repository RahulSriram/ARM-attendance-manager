import java.util.Calendar;
import java.util.Scanner;
import java.io.*;
import java.awt.Desktop;
import javax.swing.JFileChooser;

public class project
{
	public static void main(String[] args)
	{
		student s=new student();
		String rep;
		setup.firstRun('r');
		setup.makeList();
		s.init();
		do
		{
			System.out.print("Enter regno: ");
			rep=util.getString();
			
			if(rep.equals("exit"))
				System.exit(0);
			else
				s.update(rep);
		}while(true);
	}
}

class util
{
	static String getDirectory()
	{
		String directory="";
		File file=new File("delete to reset");
		
		do
		{
			try(Scanner sc=new Scanner(file))
			{
				if(!sc.nextLine().startsWith(util.getOS()))
					setup.firstRun('w');
				else
					directory=sc.nextLine();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}while(directory.equals(""));
		
		return directory;
	}
	
	static String printDays(int n)
	{
		String a="\n";
		
		for(int i=1;i<=n;i++)
		{
			if(i==1)
				a+="Monday,\n";
			if(i==2)
				a+="Tuesday,\n";
			if(i==3)
				a+="Wednesday,\n";
			if(i==4)
				a+="Thursday,\n";
			if(i==5)
				a+="Friday,\n";
			if(i==6)
				a+="Saturday,\n";
			if(i==7)
				a+="Sunday,";
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
		return (h+":"+m);
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
	
	static String getSeparator()
	{
		String a=System.getProperty("os.name").toLowerCase();
		
		if(a.startsWith("windows"))
			return "\\";
			
		else
			return "/";
	}
	
	static String getOS()
	{
		String a=System.getProperty("os.name").toLowerCase();
		
		return a;
	}
	
	static void openFile(File f)
	{
		Desktop dt = Desktop.getDesktop();
		
		try
		{
			dt.open(f);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
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
		
		return (yy+"-"+mm+"-"+dd);
	}
	
	static int getDay()
	{
		int d;
		Calendar time=Calendar.getInstance();
		
		d=time.get(Calendar.DAY_OF_WEEK);
		if(d==1)
			return 7;
			
		else
			return d-1;
	}
	
	static String getTime()
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
		String rep,name;
		File namelist=new File(util.getDirectory()+util.getSeparator()+"namelist"),timetable=new File(util.getDirectory()+util.getSeparator()+"timetable");
		
		namelist.mkdir();
		timetable.mkdir();
		do
		{
			System.out.println("1->create a new class\n2->edit timetable for an existing class\n3->edit the namelist for an existing class\n4->remove a class and all it's associated data fully");
			rep=util.getString();
			
			if(rep.equals("1"))
			{
				int i;
				System.out.print("Input the class name: ");
				name=util.getString();
				File f=new File(namelist.getPath()+util.getSeparator()+name+".csv");
				if(f.exists())
					System.out.println("The given class name already exists... Try again");
				else
				{
					String temp;
					
					try(FileWriter fw=new FileWriter(f))
					{
						i=0;						
						rep="name,regno,contact\n";
						do
						{
							i++;
							System.out.println("\nEnter Student "+ i +"'s details->\n");
							System.out.print("Enter Student's name: ");
							rep+=util.getString()+",";
							System.out.print("Enter Student's register number: ");
							rep+=util.getString()+",";
							System.out.print("Enter Student's email ID: ");
							rep+=util.getString()+"\n";
							System.out.print("Want to add more Students?[y/n]: ");
							temp=util.getString();
						}while(temp.equals("y")||temp.equals("Y"));
						fw.write(rep);
					}catch(IOException e)
					{
						e.printStackTrace();
					}
					
					rep="";
					
					f=new File(timetable.getPath()+util.getSeparator()+name+".csv");
					System.out.println("\n\nNow, create a timetable for this class\n\n");
					try(FileWriter fw=new FileWriter(f))
					{
						i=0;
						do
						{
							i++;
							System.out.println("\nInput the session "+ i +"'s timing (IN 24-HRS FORMAT):\n");
							rep+=","+util.inputTime("start")+"-"+util.inputTime("end");
							System.out.print("Add more sessions?[y/n]: ");
							temp=util.getString();
						}while(temp.equals("y")||temp.equals("Y"));
						System.out.print("How many days a week (starting from Monday) does the class have sessions on?: ");
						i=util.getInt();
						rep+=util.printDays(i);
						fw.write(rep);
					}catch(IOException e)
					{
						e.printStackTrace();
					}
					System.out.println("Press Enter to continue to input what sessions happen on each day...");
					util.getString();
					util.openFile(f);
				}
			}
			
			else if(rep.equals("2"))
			{
				File[] list=timetable.listFiles();
				File f=null;
				
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					if(list[i].getName().endsWith(".csv"))
						System.out.println(list[i].getName().replaceAll(".csv",""));
				System.out.print("Enter the class name to edit timetable for: ");
				name=util.getString();
				f=new File(timetable.getPath()+util.getSeparator()+name+".csv");
				if(f.exists())
				{
					System.out.println("Press Enter to continue to edit the timetable of class "+name+" ...");
					util.getString();
					util.openFile(f);
				}
				
				else
					System.out.println("Class not found... Try again...");
			}
			
			else if(rep.equals("3"))
			{
				File[] list=namelist.listFiles();
				File f=null;
				
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					if(list[i].getName().endsWith(".csv"))
						System.out.println(list[i].getName().replaceAll(".csv",""));
				System.out.print("Enter the class name to edit namelist for: ");
				name=util.getString();
				f=new File(namelist.getPath()+util.getSeparator()+name+".csv");
				if(f.exists())
				{
					System.out.println("Press Enter to continue to edit the namelist of class "+name+" ...");
					util.getString();
					util.openFile(f);
				}
				
				else
					System.out.println("Class not found... Try again...");
			}
			
			else if(rep.equals("4"))
			{
				rep="n";
				File[] list=namelist.listFiles();
				File f=null;
				
				System.out.println("Existing classes:");
				for(int i=0;i<list.length;i++)
					if(list[i].getName().endsWith(".csv"))
						System.out.println(list[i].getName().replaceAll(".csv",""));
				System.out.print("Enter the name of the class to remove all its data(namelist, timetable, reports): ");
				name=util.getString();
				f=new File(namelist.getPath()+util.getSeparator()+name+".csv");
				if(f.exists())
				{
					System.out.print("\n \t\tCAUTION!!\nTHIS ACTION WILL REMOVE ALL DATA ASSOCIATED TO CLASS \""+name+"\" PERMANENTLY [NAMELIST,TIMETABLE,REPORTS]!!!\n\n Do you want to continue?[y/n]: ");
					rep=util.getString();
				}
				
				else
					System.out.println("Invalid class name... Try again...");
				
				if(rep.equals("y")||rep.equals("Y"))
				{
					f=new File(namelist.getPath()+util.getSeparator()+name+".csv");
					f.delete();
					f=new File(timetable.getPath()+util.getSeparator()+name+".csv");
					f.delete();
					f=new File(util.getDirectory()+"reports");
					list=f.listFiles();
					for(int i=0;i<list.length;i++)
					{
						f=new File(list[i].getPath()+name+".csv");
						f.delete();
					}
					System.out.println("Class successfully deleted");
				}
			}
			System.out.print("Return to main menu?[y/n]: ");
			rep=util.getString();
		}while(rep.equals("y")||rep.equals("Y"));
	}	
	
	static void firstRun(char a)
	{
		String directory="";
		File file=new File("delete to reset");
		
		if(!file.exists()||a=='w')
		{
			JFileChooser f = new JFileChooser();
        	
//        	if(!file.exists())
//        		util.main(temp);
        	System.out.println("Press Enter to select the directory to store database in...");
			util.getString();
        	f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        	f.showOpenDialog(null);
			directory=f.getSelectedFile().getPath()+util.getSeparator();
        	try(FileWriter fw=new FileWriter(file))
        	{
        		fw.write(util.getOS());
        		fw.write("\n"+directory);
        	}catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
	}
	
	static void makeList()
	{
		String a="";
		File dir=new File(util.getDirectory()+"timetable");
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
					
					for(int j=0;j<util.getDay();j++)
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
					dir=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate());
					dir.mkdirs();
					dir=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+f[i].getName());
					if(!dir.exists())
					{
						try(FileWriter fw=new FileWriter(dir))
						{
							fw.write(a);
							dir=new File(util.getDirectory()+"namelist"+util.getSeparator()+f[i].getName());
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
		File dir=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate());
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
		String a=util.getTime(),b;
		session p=s.session;
		File f=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+s.batch+".csv"),f1=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+s.batch+"(1).csv");
		
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
		File dir=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+this.batch+".csv"),dir1=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+this.batch+"(1).csv");
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
		File dir=new File(util.getDirectory()+"namelist");
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
						dir=new File(util.getDirectory()+"timetable"+util.getSeparator()+f[i].getName());
						
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
							
							for(int j=1;j<util.getDay();j++)
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
						
						dir=new File(util.getDirectory()+"reports"+util.getSeparator()+util.getDate()+util.getSeparator()+f[i].getName());
						
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
		dir=new File(util.getDirectory()+"timetable"+util.getSeparator()+"bin");
				
		try(FileWriter fw=new FileWriter(dir))
		{
			fw.write(x);
		}catch(IOException e)
		{
			e.printStackTrace();
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
