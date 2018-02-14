import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

//This is the client side of the protocol. The client can send messages to the server and receive messages back from the server
public class Client{
		public static ArrayList <String> messages = new ArrayList<String>();//All messages waiting to be displayed by the client
		//This is where the client will communicate with the server
		public static void main(String[] args) throws UnknownHostException,IOException,InterruptedException{
			while(true){
		    String name; 															 
            String message;
            BufferedReader inFromUser = new BufferedReader((new InputStreamReader(System.in)));
		    System.out.print("Please enter your username: ");
            name = inFromUser.readLine();
			Socket socket = new Socket("localhost",1117);
			try{														 
                PrintWriter outToServer = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ClientThread clientthread;
                outToServer.println(name);
                outToServer.flush();
                message = inFromServer.readLine();
                if(message.equals("!!!"))
                {
                	System.out.println("You've entered an invalid username.");
                	socket.close();
                	continue;
                } 
                System.out.println(message);
                clientthread = new ClientThread(socket,name);
                clientthread.start();
                System.out.println("With ChatRooms, there are a few commands to remember:\n"
                		+ "Create:  Type 'create' followed by the name of the room you want to create.\n"
                		+ "Join:    Type 'join' followed by the name of the room you want to join.\n"
                		+ "Leave:   Type 'leave' followed by the name of the room you want to leave.\n"
                		+ "Message: Type 'message' followed by the names of the rooms you want to send\n"
                		+ "   the message to and press ENTER. From there, you will be prompted to enter\n"
                		+ "   your message.\n"
                		+ "Display: Type 'display' followed by the name of the room you want to search for.\n"
                		+ "   if you want to display all of the rooms in the system, type 'display all'.\n"
                		+ "Log Off: Type 'disconnect'.");
                while(true){
                		System.out.print("Please enter a message: ");
                        String readerInput = inFromUser.readLine();
                        if(!readerInput.equals("")){
                        	if(readerInput.compareTo("disconnect")==0){
                        		return;
                        	}
                        	if(socket.isClosed()){
                        		System.out.println("Server has disconnected. Please try again later.");
                        		return;
                        	}
                        	else if(!readerInput.isEmpty()&&!readerInput.contains(" ")||!readerInput.matches("^[ a-zA-Z0-9]*$"))
                        	{
                        		System.out.println("Invalid message format!");
                        		continue;
                        	}
                        	else if(readerInput.substring(0, readerInput.indexOf(" ")).compareTo("message") == 0){
                        		System.out.print("Enter the message you want to be relayed: ");
                        		readerInput = readerInput + ":" + inFromUser.readLine();
                        	}     
                        	outToServer.println(readerInput);
                        	TimeUnit.SECONDS.sleep(1);
                	    }
                        DisplayMessages();
                }
			}
            finally{
            	socket.close();
            }
        }
		}
		//Whenever the client sends a message or presses enter, the system will print all of the messages received from the server so far
		private static void DisplayMessages(){
			if(messages.isEmpty())return;
			for(int i = 0;i < messages.size();++i){
				System.out.println(messages.get(i));
			}
			messages.removeAll(messages);
		}
}

