package client;

import java.io.BufferedReader;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import javax.management.InstanceNotFoundException;

import javax.management.MalformedObjectNameException;

import javax.management.ReflectionException;

import org.hyperic.sigar.Sigar;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Registration;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import client.com.Sendmail; //Program used for sending warning message to a particular email id
 
public class Code implements MessageListener
{
	String OsName=System.getProperty("os.name");
	String to ="admin@keertipriya";
    XMPPConnection connection;
    double cpumost; 
    
    
    //establishing connection with openfire 
    public void login(String userName, String password) throws XMPPException
    {
    ConnectionConfiguration config = new ConnectionConfiguration("192.168.110.107");
    connection = new XMPPConnection(config);
    connection.connect();
    connection.login(userName, password);
    }
    
    //Displaying the list of user online in openfire 
    
    public void disconnect()
    {
    connection.disconnect();
    }
    
    
    public void sendMessage(String message, String to) throws XMPPException 
    { 
    Chat chat = connection.getChatManager().createChat(to, this);
    chat.sendMessage(message); 
    }
    
    //sending the details of the client to the server
    public void processMessage(Chat chat, Message message)
    {    
    if(message.getBody().equals("details"))
    {
    	new Thread(){}.start();
    	while(true){
    		 try { 
    				//Host Name
    				Chat chat1 = connection.getChatManager().createChat(to, this);	
    				String h = gethostname();
    				chat1.sendMessage(h);  
    		
    				//IP address
    				Chat chat2 = connection.getChatManager().createChat(to, this);
    				String i = getipadd();
    				chat2.sendMessage(i);  
    		
    				//CPU Usage
    				Chat chat3 = connection.getChatManager().createChat(to, this);
    		
    				try {
    			
    						double str = getCpu(i,h);
    						str = str*100;
    						float str1 = (new Double(str)).floatValue();
    						String cpu = String.valueOf(str1);
    						//System.out.println(" disk details");
    						File file = new File("c:");
    						long freeSpace =file.getTotalSpace()-file.getUsableSpace();
    						String Total_C= String.valueOf(file.getTotalSpace()/1024/1024/1024);
    						String Usable_C= String.valueOf(file.getUsableSpace()/1024/1024/1024);
    						String FreeSpace_C= String.valueOf(freeSpace/1024/1024/1024);
    	    	
    						File file1 = new File("d:");    	
    						long freeSpace1 = file1.getTotalSpace()-file1.getUsableSpace();
    						String Total_D= String.valueOf(file1.getTotalSpace()/1024/1024/1024);
    						String Usable_D= String.valueOf(file1.getUsableSpace()/1024/1024/1024);
    						String FreeSpace_D= String.valueOf(freeSpace1/1024/1024/1024);
    						String m = memory1();
    						String m1 = memory2();
    			
    						//System.out.println("entering db");
    						@SuppressWarnings("deprecation")
    						Mongo mongo = new Mongo("localhost", 27017);
    						DB db = mongo.getDB("prototype");
    			
    						BasicDBObject document = new BasicDBObject();
    			
    						document.put("Hostname", h);
			    			document.put("IpAddress", i);
			    			document.put("CPU",cpu);
			    			document.put("ctotal", Total_C);
			    			document.put("cfree", Usable_C);
			    			document.put("cused", FreeSpace_C);
			    			document.put("dtotal", Total_D);
			    			document.put("dfree", Usable_D);
			    			document.put("dused", FreeSpace_D);
			    			document.put("totalmemory", m1);
			    			document.put("freememory", m);
			    			
    			
    		                 // System.out.println("hi");
			    			DBCollection collection = db.getCollection("dummyColl");
			
			    			BasicDBObject query = new BasicDBObject();
			    			query.put("IpAddress",i);
			    			DBCursor cursor = collection.find(query);
			    			label:
			    			{
			    				while(cursor.hasNext())
			    				{
			    					BasicDBObject searchq = new BasicDBObject().append("IpAddress", i);
			    					collection.update(searchq, document);
			    					break label;
			    				}
			    				collection.insert(document);
			    					
			    			}
			    			
			    			
			    			chat3.sendMessage(cpu);
			    		  }
    				     catch (SigarException e1) {
			    			e1.printStackTrace();
			    		  		}
    				     catch (XMPPException e) {
			    			e.printStackTrace();
			    		}
						
			    	
			    	//Disk Usage
			    	Chat chat4 = connection.getChatManager().createChat(to, this);
					
			    	if(OsName.equals("Linux"))
			    	{    		   	
			    		 File[] roots = File.listRoots();
			
			 		    /* For each filesystem root, print some info */
			 		    for (File root : roots) {
			 		    	
			 		    	try {
			 		    	 long UsedSpace =root.getTotalSpace()-root.getFreeSpace();	
			 		    	 String FreeSpace= String.valueOf(UsedSpace/1024/1024/1024);
			 		    	 chat4.sendMessage("File system root: "+root.getAbsolutePath()+"\n"+
			 		    	"Total space (Gb): "+root.getTotalSpace()/1024/1024/1024+"\n"+
								"Free space (Gb): " + root.getFreeSpace()/1024/1024/1024
			 		    	+"\n"+"UsedSpace (Gb): " + FreeSpace);
								
			 		    	} catch (XMPPException e) {
									e.printStackTrace();
								} 		     		
			 		    }
			 		    }
			    	else{
			    		{    		   	
			    	    	File file = new File("c:");
			    	        long freeSpace =file.getTotalSpace()-file.getUsableSpace();
			    	    	String FreeSpace_C= String.valueOf(freeSpace/1024/1024/1024);
			    	    	
			    			File file1 = new File("d:");    	
			    	    	long freeSpace1 = file1.getTotalSpace()-file1.getUsableSpace(); //unallocated / free disk space in bytes.   	 		
			    			String FreeSpace_D= String.valueOf(freeSpace1/1024/1024/1024);
			    			try {
			    				
			    				chat4.sendMessage("\n"+"Operating System name is =" + OsName +"\n"+ 
			    						"C Drive Total Memory(GB) is=" + file.getTotalSpace()/1024/1024/1024
			    						+"\n"+ "C Drive Used Memory(GB) is =" + file.getUsableSpace()/1024/1024/1024+
			    						"\n"+"C Drive Free Memory(GB) is=" +FreeSpace_C  +"\n"+
			    						"D Drive Total Memory(GB) is = "+file1.getTotalSpace()/1024/1024/1024 +"\n"+
			    						"D Drive Used Memory(GB) is =" +file1.getUsableSpace()/1024/1024/1024+"\n"+ 
			    						"D Drive Free Memory(GB) is=" +FreeSpace_D);			
			    				
			    			} catch (XMPPException e) {			
			    				e.printStackTrace();
			    			}
			    		
			    	     }
			    	}
			    	
			    	//Services
			    	Chat chat5 = connection.getChatManager().createChat(to, this);
					try {	
						if(OsName.equals("Linux")){
						chat5.sendMessage("\n"+"USER"+"\t"+"PID"+"\t"+"%CPU"+"\t"+"%MEM"+"\t"+"VSZ"+"\t"+"RSS"
								+"\t"+"TTY"+"\t"+"STAT"+"\t"+"START"+"\t"+"TIME"+"\t"+"COMMAND"
								+"\n"+showProcessData()+"\n");
						}
						else
						{
							chat5.sendMessage("\n"+showProcessData()+"\n");
						}
					} catch (XMPPException e) {
						e.printStackTrace();
					}
			    	
					 Chat chat7 = connection.getChatManager().createChat(to, this);
					    try{
					    	String m = memory1();
					        chat7.sendMessage(m);
					    }
					    catch(XMPPException e)
					    {
					    	e.printStackTrace();
					    }
					    Chat chat8 = connection.getChatManager().createChat(to, this);
					    try{
					    	String m1 = memory2();
					        chat8.sendMessage(m1);
					    }
					    catch(XMPPException e)
					    {
					    	e.printStackTrace();
					    }
					    
    		 } catch (XMPPException e) {		
					e.printStackTrace();
					} catch (IOException e) {			
					e.printStackTrace();
					}
    		 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
    	 }
     }
			    else{
			    	System.out.println(chat.getParticipant() + " says: " + message.getBody());
			    }
    	}
    
   /* Chat chat6 = connection.getChatManager().createChat(to, this);	
	try{
    chat6.sendMessage(network());}  
	catch (SigarException e){
		e.printStackTrace();
	}
	catch (InterruptedException e){
		e.printStackTrace();
	}
	catch (XMPPException e) {
		e.printStackTrace();
	}*/
       
   
    
    
    
    
    
    
    public String getipadd() throws UnknownHostException, XMPPException
	 {
			InetAddress inetAddr = InetAddress.getLocalHost();			
				byte[] addr = inetAddr.getAddress();
				String ipAddr = "";
				for (int i = 0; i < addr.length; i++) {
					if (i > 0) {
						ipAddr += ".";
					}
					ipAddr += addr[i] & 0xFF;											
		}
				return ipAddr;
	    }
    public String gethostname() throws XMPPException, IOException{    	
		InetAddress inetAddr = InetAddress.getLocalHost();	
		String hostname = inetAddr.getHostName();
		return hostname;
	}
    public  Double getCpu(String ipaddress,String host) throws SigarException
   	{		
    	//System.out.println("2");
    	Sigar sigar=new Sigar();
        CpuPerc cpu = sigar.getCpuPerc();
        //double system = cpu.getSys();
        double user = cpu.getUser();
        String c1 = CpuPerc.format(user);
        Date date = new Date();
        String date1 = date.toString(); 
        if(user>0.80000)
        {
        	//System.out.println("3");
        	Sendmail.mail("<html><body><div style=\"background-color:red;\"><pre>    <img src=http://www.relevancelab.com/images/diagram-01.png  width = \"50\" height=\"50\" style=\"margin-left:15px; padding:5px; float:left\"/> <h1 style=\"color:#00539E\">SRM System</h1><hr/></div></pre><h1 style=\"color:red\">ALERT!!!</h1><p> The CPU Value has exceeded above 80%. For further details refer the link below</p><a href=\"http://192.168.110.107:3000\" > CPU above 80% </a><p> The exceeded details are as follows</p><table style = \"margin:10px; padding:5px\" ><tr ><th style = \"margin:10px; padding:5px\">Host Name</th><th></th><th style = \"margin:10px; padding:5px\">CPU %</th><th></th><th style =\"margin:10px; padding:5px\"> Time </th></tr><tr><td style = \"margin:10px; padding:5px\">" +host + "</td><td></td><td style = \"margin:10px; padding:5px\">" + c1 +"</td><td></td><td style = \"margin:10px; padding:5px\">"+ date1 +"</td></tr></body></html>");
        }
        if(user<0.20000)
        {
        	//System.out.println("user below");
        	Sendmail.mail("<html><body><div style=\"background-color:#81D8D0;\"><pre>    <img src=http://www.relevancelab.com/images/diagram-01.png  width = \"50\" height=\"50\" style=\"margin-left:15px; padding:5px; float:left\"/> <h1 style=\"color:#00539E\">SRM System</h1><hr/></div></pre><h1 style=\"color:#81D8D0\">REMAINDER!!!</h1><p> The CPU Value has fallen below 20%. For further details refer the link below</p><a href=\"http://192.168.110.107:3000\" > CPU below 20% </a><p> The fallen details are as follows</p><table style = \"margin:10px; padding:5px\" ><tr ><th style = \"margin:10px; padding:5px\">Host Name</th><th></th><th style = \"margin:10px; padding:5px\">CPU %</th><th></th><th style =\"margin:10px; padding:5px\"> Time </th></tr><tr><td style = \"margin:10px; padding:5px\">" +host + "</td><td></td><td style = \"margin:10px; padding:5px\">" + c1 +"</td><td></td><td style = \"margin:10px; padding:5px\">"+ date1 +"</td></tr></body></html>");

        }
        
       // double idle = cpu.getIdle();
        System.out.println("The host name is :" + host);
        cpumost = find(ipaddress)/100;
        maxcpu(user,date,ipaddress);
        
      System.out.println(/*"idle: " +CpuPerc.format(idle) +", system: "+CpuPerc.format(system)+ */" user: "+user);
      try{
  		
    	  //System.out.println("Enter MD");
    	  @SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost",27017); 
    	  DB db1=mongo.getDB("prototype");
    	  DBCollection coll1=db1.getCollection("trace"+host);
    	  double str2 =user;
			str2 = str2*100;
			int str3 = (new Double(str2)).intValue();
			//String cpu1 = String.valueOf(str3);
			
    	  BasicDBObject document1 = new BasicDBObject();
    	  document1.put("cpu",str3);
    	  document1.put("Time",date1);
    	  document1.put("Ipaddress",ipaddress);
    	  coll1.insert(document1);
    	    
			BasicDBObject quer2 = new BasicDBObject();
			quer2.put("_id", -1);
			DBCursor cursor6 = coll1.find().sort(quer2).limit(10);		
			DBCollection collection2 = db1.getCollection("dispaly"+host);
			collection2.remove(new BasicDBObject());
			while(cursor6.hasNext())
			{   
				
				collection2.insert(cursor6.next());
				
			}
			
   	 
    	 if(user<.2 || user>.8){
    		 	 
						
					DBCollection collection1 = db1.getCollection("complete"+host);
	    			collection1.insert(document1);
	    			BasicDBObject query2 = new BasicDBObject();
	    			query2.put("_id", -1);
	    			DBCursor cursor1 = collection1.find().sort(query2).limit(5);		
	    			DBCollection collection3 = db1.getCollection("Last"+host);
	    			collection3.remove(new BasicDBObject());
	    			while(cursor1.hasNext())
	    			{
	    				collection3.insert(cursor1.next());
	    				
	    			}
   	      	  
    	 }
   	       }
   		   catch (IOException e) {			
    			e.printStackTrace();
 			}
      
    //if the cpu value is b/w 65%-100% then put it in the collection host100
      if (user > 0.650000){
    	try{
    	 @SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost",27017); 
    	  DB db=mongo.getDB("prototype");
    	  DBCollection coll=db.getCollection(host+"100");
    	  double str =user;
			str = str*100;
			float str1 = (new Double(str)).floatValue();
			String cpu1 = String.valueOf(str1);
			
    	  BasicDBObject document = new BasicDBObject();
    	  document.put("cpu",cpu1);
    	  document.put("Time",date1);
    	  document.put("Ipaddress",ipaddress);
    	  
    	  BasicDBObject query = new BasicDBObject();
			query.put("Ipaddress",ipaddress);
			DBCursor cursor = coll.find(query);
			label:
			{
				while(cursor.hasNext())
				{
					
					coll.insert(document);
					graphval(host+"100");
					break label;
				}	
				coll.insert(document);
			}
			
    	   	      	  
    	  
   	       }
   		   catch (IOException e) {			
    			e.printStackTrace();
 			}
      }
      
      //if the cpu value is between 35%-65% then the cpu value is put in collection host60
      else if(user>0.35 && user<=0.65)
      {
    	  try
    	  {
    	  @SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost",27017); 
    	  DB db=mongo.getDB("prototype");
    	  DBCollection coll=db.getCollection(host+"65");
    	  double str =user;
			str = str*100;
			float str1 = (new Double(str)).floatValue();
			String cpu1 = String.valueOf(str1);
			
    	  BasicDBObject document = new BasicDBObject();
    	  document.put("cpu", cpu1);
    	  document.put("Time",date1);
    	  document.put("Ipaddress",ipaddress);
    	  
    	  BasicDBObject query = new BasicDBObject();
			query.put("Ipaddress",ipaddress);
			DBCursor cursor = coll.find(query);
			label:
			{
				while(cursor.hasNext())
				{
					coll.insert(document);
					graphval(host+"65");
					break label;
				}
				coll.insert(document);
					
			}
			
    	      	      	  
    	     	       }
   		   catch (IOException e) {			
    			e.printStackTrace();
 			}
      }
      
      //if the cpu value is b/w 0-35% then put it in the collection host30
      else if(user>=0.0 && user<=0.35)
      {
    	  try
    	  {
    	  @SuppressWarnings("deprecation")
		  Mongo mongo = new Mongo("localhost",27017); 
    	  DB db=mongo.getDB("prototype");
    	  DBCollection coll= db.getCollection(host+"30");
    	    double str =user;
			str = str*100;
			float str1 = (new Double(str)).floatValue();
			String cpu1 = String.valueOf(str1);
			
    	  BasicDBObject document = new BasicDBObject();
    	  document.put("cpu",cpu1);
    	  document.put("Time",date1);
    	  document.put("Ipaddress",ipaddress);
    	  
    	  BasicDBObject query = new BasicDBObject();
			query.put("Ipaddress",ipaddress);
			DBCursor cursor = coll.find(query);
			label:
			{
				while(cursor.hasNext())
				{
					coll.insert(document);
					graphval(host+"30");
					break label;
				}
				
				coll.insert(document);
			}
    	      	      	  
    	  
   	       }
   		   catch (IOException e) {			
    			e.printStackTrace();
 			} 
      }
      
      return user;
   		
   	   }
   
   

    //For retaining the highest value in maxusage collection (if the newly entered value is not greater than previous value)
    public double find(String ip)
    {
    	double value=0;
    	try
    	{
    		//System.out.println("Entering find");
       @SuppressWarnings("deprecation")
	   Mongo mongo = new Mongo("localhost",27017);
       DB db = mongo.getDB("prototype");
       DBCollection coll = db.getCollection("maxusage");
       BasicDBObject query = new BasicDBObject();
       query.put("Ipaddress", ip);
       DBCursor cursor = coll.find(query);
       label:
       {
    	   while(cursor.hasNext())
       {
    	   String value1 = (String)cursor.next().get("cpumax");
    	   value = Double.parseDouble(value1);
    	   //System.out.println("Value" + value);
    	   break label;
       }
       }
    	}catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	
    	
		return value;
    }
        //For putting the max value in separate collection
    public void maxcpu(double cpu, Date date, String ip)
    {
    	try
    	{
    	@SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost",27017);
    	DB db = mongo.getDB("prototype");
    	DBCollection coll=db.getCollection("maxusage");
       	BasicDBObject query = new BasicDBObject();
       	query.put("Ipaddress", ip);
       	String time = date.toString();
       	DBCursor cursor = coll.find(query);
       	label:
       	{
       	while(cursor.hasNext())
       	{	
       	      	
		if(cpumost <= cpu)
       	{
			cpumost=cpu;
       		cpu=cpu*100;
			String cu = String.valueOf(cpu);
       		BasicDBObject doc = new BasicDBObject();
       		doc.put("cpumax",cu );
       		doc.put("Time",time);
       		doc.put("Ipaddress", ip);
       		BasicDBObject searchq = new BasicDBObject().append("Ipaddress", ip);
			coll.update(searchq, doc);
       	}
		break label;
    	}
       	cpumost=cpu;
       	cpu= cpu*100;
       	String cu1 = String.valueOf(cpu);
       	BasicDBObject doc1 = new BasicDBObject();
       	doc1.put("cpumax", cu1);
       	doc1.put("Time", date);
       	doc1.put("Ipaddress", ip);
       	coll.insert(doc1);
       	}
    	}catch (IOException e) {			
			e.printStackTrace();
			}
    } 
    
    //For putting the last 5 values of each collection into another collection and displaying it in the graph
    public void graphval(String hs){
    	try{
    	BasicDBObject query = new BasicDBObject();
    	query.put("cpu", -1);
    	@SuppressWarnings("deprecation")
		Mongo mongo = new Mongo("localhost", 27017);
    	DB db = mongo.getDB("prototype");
    	DBCollection collection = db.getCollection(hs);
    	DBCursor cursor = collection.find().sort(query).limit(5);
    	  
    		//label:
    		{
    			
    		 DBCollection coll1 = db.getCollection("mummy1");
			  coll1.remove(new BasicDBObject());
    		  while(cursor.hasNext())
    		 {
    			  
    			  coll1.insert(cursor.next());
    			 // System.out.println(cursor.next());
    			  

    			  
    		 }
    		 
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	}
   
  
   
    		
    private String GetProcessListData()
    {
    Process p;
    Runtime runTime;
    String process = null;
    try {
    //Get Runtime environment of System
    runTime = Runtime.getRuntime(); 
    //Execute command thru Runtime
    String OsName=System.getProperty("os.name");
    if(OsName.equals("Windows 8")||(OsName.equals("Windows 7"))){
    	 p = runTime.exec("tasklist"); 
    }
    else{
    	p=runTime.exec("ps ux");	
    }
    
    //Create Inputstream for Read Processes
    InputStream inputStream = p.getInputStream();
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader); 
    //Read the processes from system and add & as delimeter for tokenize the output
    String line = bufferedReader.readLine();
    System.out.println(line);
    process = "\n";
    while (line != null) {
    line = bufferedReader.readLine();
    process += line + "\n";
    } 
    //Close the Streams
    bufferedReader.close();
    inputStreamReader.close();
    inputStream.close();
    } catch (IOException e) {
    System.out.println("Exception arise during the read Processes");
    e.printStackTrace();
    }
    return process;
    }
    private String showProcessData()
    { 
    //Call the method For Read the process
    String proc = GetProcessListData();    
	return proc;
    }
    
    @SuppressWarnings("restriction")
	public String memory1(){
    	int mb=1024*1024;
    	
    	com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
		           java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    	
		long a = os.getFreePhysicalMemorySize()/mb;
		String mem1 = String.valueOf(a);
		return mem1;
    }
    
    @SuppressWarnings("restriction")
	public String memory2(){
    	int mb=1024*1024;
    	
    	com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
		           java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    	long b = os.getTotalPhysicalMemorySize()/mb;
    	String mem2 = String.valueOf(b);
    	return mem2;
    }
     
    @SuppressWarnings("unused")
	public static void main(String args[]) throws XMPPException, IOException, MalformedObjectNameException, InstanceNotFoundException, ReflectionException
    {
    	Code c = new Code();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        c.getipadd();
        ConnectionConfiguration connConfig = new ConnectionConfiguration("192.168.110.107", 5222);
                XMPPConnection connection = new XMPPConnection(connConfig);            
                connection.connect();
                AccountManager accountManager = connection.getAccountManager();
                Map<String, String> attributes = new HashMap<String, String>();
                System.out.println("Enter your required info to  register in server");
                System.out.println("===================");
                //System.out.println("Enter Username--");
                String username = c.getipadd();          
                attributes.put("username", username);
                //System.out.println("Enter password--");
                String password = "123";
                attributes.put("password",password );
                String k = "@keertipriya.com";
                //System.out.println("Enter  mail(e.g.=test@ganya.com)--");
                String email = c.getipadd().concat(k);
                attributes.put("email", email);
                //System.out.println("Enter  your Name--");
                String name = c.gethostname();
                attributes.put("name", name);
                accountManager.createAccount(username, password,attributes);
                Registration registration = new Registration();
                registration.setType(IQ.Type.SET);
                registration.setTo(connection.getServiceName());
                PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));
                PacketCollector collector = connection.createPacketCollector(filter);
                connection.sendPacket(registration); 
                System.out.println("Congratulations!! your registration completed!!!");
                System.out.println("Provide Your credential for login ");
                System.out.println("Enter Username--");
                String username1 = username;
                System.out.println("Enter password--");
                String password1 = password;
        c.login(username1, password1);    
        System.out.println("-----"); 
        String talkTo = "admin@keertipriya"; 
        System.out.println("-----");
        System.out.println("All messages will be sent to " + talkTo);
        c.sendMessage("hello", talkTo);
        System.out.println("Enter your message in the console:");
        System.out.println("-----\n"); 
        while( !(msg=br.readLine()).equals("bye"))
        {
        	
            c.sendMessage(msg, talkTo);
           
        } 
        c.disconnect();
        System.exit(0);
        }
}