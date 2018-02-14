import java.io.*;
import java.net.*;
import java.util.*;
//This is a thread object that will be used by each client in the server
public class ServerThread extends Thread {
        private Socket socket; //Socket of the current user
        private String name;//Username of the current user
        private PrintWriter OutToClient;//Allows the server to send messages to the client
        //Creates the thread
        ServerThread(Socket socket,String name){
                this.socket = socket;
                this.name = name;
        }
        //Will allow callers to start the thread. This is were the server will parse and interpret a client's messages.
        public void run(){
                try{
                	    boolean connected = true;
                        String message1;
                        String message2;
                        String message3;
                        Scanner InFromUser = new Scanner(socket.getInputStream());
                        OutToClient = new PrintWriter(socket.getOutputStream(),true);
                        while(connected){
                        	    if(InFromUser.hasNext()){
                        	    	message1 = InFromUser.nextLine();
                        	    	System.out.println("Incoming message from " + name + ": " +message1);
                        	    	if(!message1.isEmpty()&&!message1.contains(" "))
                        	    	{
                        	    		OutToClient.println("Server cannot interpret your message!");
                        	    		continue;
                        	    	}
                        	    	message2 = message1.substring(message1.indexOf(" ")+1);
                        	    	message1 = message1.substring(0, message1.indexOf(" "));
                        	    	
                        	    	if(message1.compareTo("message") == 0){
                        	    		message3 = message2.substring(message2.indexOf(":")+1);
                        	    		message2 = message2.substring(0,message2.indexOf(":"));
                        	    		RelayMessage(message2,message3);
                        	    		System.out.println(name +"'s message sucessfully sent");
                        	    	}
                        	    	else if(message1.compareTo("create") == 0){
                        	    		if(!message2.matches("^[a-zA-Z0-9]*$")){
                            	    		OutToClient.println("This is not a valid room name.");
                            	    	}
                        	    		else if(FindRoom(message2)){
                        	    			System.out.println(message2 + " is already the name of a chatroom.");
                        	    			OutToClient.println(message2 + " is already the name of a chatroom.");
                        	    		}
                        	    		else CreateRoom(message2,name,socket);
                        	    	}
                        	    	else if(message1.compareTo("join") == 0){
                        	    		JoinRoom(message2);
                        	    		System.out.println(name +" has been added to " + message2 + ".");
                        	    	}
                        	    	else if(message1.compareTo("leave") == 0){
                        	    		LeaveRoom(message2);
                        	    	}
                        	    	else if(message1.compareTo("display")== 0){
                        	    		if(message2.compareTo("all") == 0)DisplayRooms();
                        	    		else DisplayUsers(message2);
                        	    		System.out.println("Information sent to "+ name + ".");
                        	    	}
                        	    	else OutToClient.println("Server cannot interpret your message!");
                        	    		
                        	    }
                        	    else{
                        	    	Status();
                        	    	InFromUser.close();
                        	    	connected = false;
                        	    }
                        }
                        socket.close();
                }catch(IOException e){
                   e.printStackTrace();
                 }
        }
        //Checks to see if the socket connection is still live
        public int Status(){
        	boolean connected = socket.isConnected() && socket.isClosed();
        	if(!connected){
        		for(int i = 0;i < Server.ActiveUserNames.size();++i){
        			if(name.equals(Server.ActiveUserNames.get(i)))
        				Server.ActiveUserNames.remove(i);
        		}
        		RemoveFromRooms();
        		System.out.println(name + " has disconnected...");
        		return 0;
        	}
        	return 1;
        }
        //Check to see if a room exists in the system
        public boolean FindRoom(String RoomToFind){
        	for(int i = 0; i < Server.ChatRooms.size();++i){
        		if(Server.ChatRooms.get(i).getRoomName().compareTo(RoomToFind) == 0){
        			return true;
        		}
        	}
        	return false;
        }
        //Add a ChatRoom object to the Server's ArrayList of ChatRooms
        public void CreateRoom(String NewRoomName,String name, Socket userSocket){
        	if(Server.ChatRooms.size() >= 10){
        		OutToClient.println("Cannot create more then ten ChatRooms at a time.");
        		return;
        	}
        	String message = "You've created " + NewRoomName + " and have been added to it.";
        	System.out.println(NewRoomName + " has been created...");
        	ChatRoom temp = new ChatRoom(NewRoomName,name,userSocket);
        	Server.ChatRooms.add(temp);
        	OutToClient.println(message);
        }
        //Join a ChatRoom in the system
        public void JoinRoom(String RoomToJoin) throws IOException{
        	String message;
        	for(int i = 0; i < Server.ChatRooms.size();++i){
        		if(Server.ChatRooms.get(i).getRoomName().compareTo(RoomToJoin) == 0){
        			if(Server.ChatRooms.get(i).FindUser(name)){
        				message = "You are already a member of "+ RoomToJoin;
        				OutToClient.println(message);
        			}
        			else{
        				Server.ChatRooms.get(i).setMemberNames(name);
        				Server.ChatRooms.get(i).setMemberSockets(socket);
        				message = name + " has joined " + RoomToJoin + ".";
        				RelayMessage(message,i);
        			}
        			return;
        		}
        	}
        	OutToClient.println("That ChatRoom name doesn't exist...");
        }
        //Leave a ChatRoom in the system
        public void LeaveRoom(String RoomToLeave) throws IOException{
        	for(int i = 0; i < Server.ChatRooms.size();++i){
        		if(Server.ChatRooms.get(i).getRoomName().compareTo(RoomToLeave) == 0){
        			if(Server.ChatRooms.get(i).RemoveUser(socket,name)){
        				System.out.println(name + " has been removed from " + RoomToLeave);
        				OutToClient.println("You've been removed from " + RoomToLeave);
        			}
        			else{
        				System.out.println(name + " is not a member of " + RoomToLeave);
        				OutToClient.println("You are not a member of "+ RoomToLeave);
        			}
        			return;
        		}
        	}
        	System.out.println(name + "wasn't removed from any rooms.");
    		OutToClient.println(RoomToLeave + " is not the name of a chatRoom");
        }
        //Basically a wrapper function for RelayMessage(String MessageToRelay,int i)
        public void RelayMessage(String FindRooms,String MessageToRelay) throws IOException{
        	String currentRoom;
        	for(int i = 0;i < Server.ChatRooms.size();++i){
        		currentRoom = Server.ChatRooms.get(i).getRoomName();
        		if(FindRooms.contains(currentRoom)){
        			if(Server.ChatRooms.get(i).FindUser(name)){
        				RelayMessage(currentRoom+ ": " + name + ": "+ MessageToRelay,i);
        			}
        			else OutToClient.println("You are not a member of " + currentRoom);
        		}
        	}
        }
        //Remove a user from the current ChatRoom
        public void RelayMessage(String MessageToRelay,int i) throws IOException{
        	for(int j = 0;j < Server.ChatRooms.get(i).getUserNumber();++j){
        		Socket current = Server.ChatRooms.get(i).getMemberSockets(j);
        		PrintWriter temp = new PrintWriter(current.getOutputStream(),true);
        		temp.println(MessageToRelay);
        		temp.flush();
        	}
        }
        //Display all of the ChatRooms in the system
        public void DisplayRooms(){
        	OutToClient.println("These are the rooms currently on the system:");
        	for(int i = 0;i < Server.ChatRooms.size();++i){
        		OutToClient.println(Server.ChatRooms.get(i).getRoomName());
        	}
        }
        //Display all users in a ChatRoom
        public void DisplayUsers(String RoomName){
        	for(int i = 0; i < Server.ChatRooms.size();++i){
        		if(Server.ChatRooms.get(i).getRoomName().compareTo(RoomName) == 0){
        			OutToClient.println("These are the users currently in "+RoomName);
        			Server.ChatRooms.get(i).DisplayUsers(OutToClient);
        			return;
        		}
        	}
        	OutToClient.println(RoomName+ " is not a room.");
        }
        //Remove the current user from the ChatRoom
        public void RemoveFromRooms(){
        	for(int i = 0;i < Server.ChatRooms.size();++i){
        		Server.ChatRooms.get(i).RemoveUser(socket, name);
        	}
        }
}
