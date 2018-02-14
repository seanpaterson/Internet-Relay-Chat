import java.net.*;
import java.io.*;
import java.util.*;
//The part of the program where the server will accept new client sockets
public class Server{
        public static final int PORT = 1117; //Server will speak over port 117
        public static String name; //Hold the name of the current client
        public static ArrayList <String> ActiveUserNames = new ArrayList<String>();//All users names in the system
        public static ArrayList <ChatRoom> ChatRooms = new ArrayList<ChatRoom>();//All ChatRooms in the system
        //Runs the server class
        public static void main(String[] args)throws IOException{
                new Server().runServer();
        }
        //Constantly accepts new client sockets
        public void runServer() throws IOException{
        	ServerSocket serverSocket = new ServerSocket(PORT);
        	try{
                System.out.println("Server is ready to go...");
                while (true){
                        Socket socket = serverSocket.accept();
                        if(addUser(socket) == 1)
                        	new ServerThread(socket,name).start();
                        else{
                        	socket.close();
                        }
                }
        	}
        	finally{
        		serverSocket.close();
        	}
        }
        //Adds new client sockets to the system
        public int addUser(Socket toAdd) throws IOException{
        	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(toAdd.getInputStream()));
        	PrintWriter outToClient = new PrintWriter(toAdd.getOutputStream(),true);
        	name = inFromClient.readLine();
        	if((name.matches("^[a-zA-Z0-9]*$")&&name != null&& !name.isEmpty())&& findUser(name) == 0){
        		ActiveUserNames.add(name);
        		System.out.println(name + " has entered the system.");
        		outToClient.println("Welcome to ChatRooms " + name);
        		return 1;
        	}
        	else{
        		System.out.println(name + " is not a valid nickname. Sending error message to client now...");
        		outToClient.println("!!!");
        		return 0;
        	}
        }
        //Check to see if a user is already in the system.
        public int findUser(String name){
        	for(int i = 0; i < ActiveUserNames.size();++i){
        		if(name.equals(ActiveUserNames.get(i)))
        			return 1;
        	}
        	return 0;
        }
}