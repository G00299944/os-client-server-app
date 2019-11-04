package ie.gmit.sw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) {
		
		ServerSocket listener;
		int clientid = 0;
		
		try {
			listener = new ServerSocket(10000, 10);
			
			while (true) {
				System.out.println("main thread listening for incoming connections");
				Socket newConnection = listener.accept();
				
				System.out.println("new connection received and spanning a thread");
				//ConnectionHandler t = new ConnectionHandler(newConnection, clientid);
				clientid++;
				
				Thread t = new Thread(new ConnectionHandler(newConnection, clientid));
				t.start();
				
				
			}
		}
		catch (IOException e) {
			System.out.println("socket not opened");
			e.printStackTrace();
		}
	}
}

class ConnectionHandler implements Runnable {
	
	// Member Variables
	Socket individualConnection;
	int socketId;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	int answer;
	
	// Constructor
	public ConnectionHandler(Socket individualConnection, int socketId) {
		this.individualConnection = individualConnection;
		this.socketId = socketId;
	}
	
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("client> " + msg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		try {
			out = new ObjectOutputStream(individualConnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(individualConnection.getInputStream());
			System.out.println("connection" + socketId + "from ip: " + individualConnection.getInetAddress());
		
		
			do {
				sendMessage("enter 1 to add or 2 to square root");
				message = (String)in.readObject();
				
			}while(!message.contentEquals("1") && !message.equals("2"));
			
			if(message.contentEquals("1")) {
				answer = 0;
				
				sendMessage("num1: ");
				message = (String)in.readObject();
				answer += Integer.parseInt(message);
				
				sendMessage("num2: ");
				message = (String)in.readObject();
				answer += Integer.parseInt(message);
				
				sendMessage("answer: " + answer);
			}
			else if (message.equals("2")) {
				answer = 0;
				
				sendMessage("num: ");
				message = (String)in.readObject();
				answer = (int) Math.sqrt(Double.parseDouble(message));
				
				sendMessage("answer: " + answer);
			}
		
			sendMessage("app is finnished");
		
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				out.close();
				in.close();
				individualConnection.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
