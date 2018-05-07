

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;

public class MasterBot {
	static int Port;
	public ServerSocket s1;
	public static ArrayList<Client_connections> clientList = new ArrayList<Client_connections>();
	static String input_command;
	static int No_of_conn;

	public static void main(String[] args) throws IOException {
		
		String command5 =  null;
		if (args.length < 2) {
			System.out.println("Incorrect command");
			System.exit(0);
		}
		else if (args.length == 2) {
			if (args[0] != null && args[0].equals("-p")) {
				Port = Integer.parseInt(args[1]);
			}
			else {
				System.out.println("Incorrect Command");
				System.exit(0);
			}
		}
		ServerSocket server_socket = null;
		try {
			server_socket = new ServerSocket(Port);
		} catch (IOException e1) {
			e1.getMessage();
		}
		Thread t = new Thread(new SocketClientAccept(server_socket, Port), "My Thread");
		t.start();
		while (true) {
			System.out.print(">");
			Scanner sc = new Scanner(System.in);
			input_command = sc.nextLine();
			String[] command = input_command.split(" ");
			
			
			if (command[0].equals("list")) {
				for (int i = 0; i < clientList.size(); i++) {
					System.out.println("Address:" + clientList.get(i).Slave_name + " " + clientList.get(i).Slave_address
							+ " " + clientList.get(i).Slave_port + " " + clientList.get(i).date);
				}
			}
			
			//User gave the command to connect
			else if (command[0].equals("connect")) {
				if (command.length < 4) {
					System.out.println("Incorrect connect command");
				} else 
				{
					if(command.length==4)
					{
						No_of_conn = 1;
					}
					else if (command.length == 6) 
					{
						No_of_conn = Integer.parseInt(command[4]);
						command5=command[5];
					} 
					else if (command.length==5)
					{
						if (command[4].matches("^[0-9]+$")) 
						{
							No_of_conn = Integer.parseInt(command[4]);
						}
						else if (command[4].equalsIgnoreCase("keepalive")|| command[4].matches("^url=[^ ]+$") ) {
							No_of_conn = 1;
							command5=command[4];
						} 
					}
					else
					{
						System.out.println("Incorrect connect command with more than 6 args");
					}
					Iterator<Client_connections> i = clientList.iterator();
					if (command[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true) 
					{
						int found = 0;
						while (i.hasNext()) 
						{
							Client_connections clicon1 = i.next();
							
							if (clicon1.Slave_address.equals(command[1]))
							{
								found = 1;
								PrintStream p = new PrintStream(clicon1.Slave_socket.getOutputStream());
								if(command5==null)
								{
									p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn );
									p.flush();
								}
								else
								{
										p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn + " " + command5);
										p.flush();
								}
							}
						}
						if (found == 0) {
							System.out.println("Slave address not found");
						}
					}

					else if (command[1].equals("all")) {
						while (i.hasNext()) {
							Client_connections clicon1 = i.next();
							PrintStream p = new PrintStream(clicon1.Slave_socket.getOutputStream());
							if(command5==null)
							{
								p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn);
								p.flush();
							}
							else
							{						
								p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn + " " + command5);
								p.flush();
							}
						}
					}
					else {
						int found = 0;
						while (i.hasNext()) {
							Client_connections clicon1 = i.next();
							if (clicon1.Slave_name.equals(command[1])) {
								found = 1;
								PrintStream p = new PrintStream(clicon1.Slave_socket.getOutputStream());
								if(command5==null)
								{
									p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn);
									p.flush();
								}
								else
								{
									p.println("connect " + command[2] + " " + command[3] + " " + No_of_conn + " " + command5);
									p.flush();
								}
							}
						}
						if (found == 0) {
							System.out.println("Slave name not found");
						}
					}
				}
			}
			
			else {
				System.out.println("Error: Incorrect command");
			}
		}
	}
}

class SocketClientAccept implements Runnable {
	int Port;
	ServerSocket server_socket;

	SocketClientAccept(ServerSocket serversoc, int p) {
		Port = p;
		server_socket = serversoc;
	}

	//created a run method
	public void run() {

		while (true) {
			Socket client_socket = null;
			Client_connections clicon = new Client_connections();
			try {
				client_socket = server_socket.accept();
				clicon.Slave_name = client_socket.getInetAddress().getHostName();
				clicon.Slave_address = client_socket.getInetAddress().getHostAddress();
				clicon.Slave_port = client_socket.getPort();
				clicon.Slave_socket = client_socket;
				clicon.date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
				MasterBot.clientList.add(clicon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
