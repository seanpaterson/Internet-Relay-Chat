import java.net.*;
import java.io.*;
import java.util.*;

//An individual ChatRoom object. Each ChatRoom will have it's own name and users currently in that ChatRoom
public class ChatRoom {
	private String RoomName; //Name of this ChatRoom object
	private ArrayList <Socket> MemberSockets = new ArrayList<Socket>();//All client sockets in the room
	private ArrayList<String> MemberNames = new ArrayList<String>();//All client names in the room
	//Constructor for the room. Adds the room's name and the first client's information into the ChatRoom
	ChatRoom(String RoomName,String UserName, Socket UserSocket){
		this.RoomName = RoomName;
		this.MemberSockets.add(UserSocket);
		this.MemberNames.add(UserName);
	}
	//Returns the RoomName to the calling routine
	public int getUserNumber(){
		return MemberSockets.size();
	}
	public String getRoomName() {
		return RoomName;
	}
	//Changes the RoomName
	public void setRoomName(String roomName) {
		RoomName = roomName;
	}
	//Returns a client socket to the calling routine
	public Socket getMemberSockets(int i) {
		return MemberSockets.get(i);
	}
	//Adds a member socket to the ChatRoom
	public void setMemberSockets(Socket memberSockets) {
		MemberSockets.add(memberSockets);
	}
	//Returns a client's username to the calling routine
	public String getMemberNames(int indexToFind) {
		return MemberNames.get(indexToFind);
	}
	//Adds a client's username to the ChatRoom
	public void setMemberNames(String memberName) {
		MemberNames.add(memberName);
	}
	//Find a user in the current room
	public boolean FindUser(String toFind){
		for(int i = 0; i < MemberNames.size();++i){
			if(MemberNames.get(i).compareTo(toFind)==0)return true;
		}
		return false;
	}
	//Remove the remnants of a client from the room
	public boolean RemoveUser(Socket RemoveSocket,String RemoveUsername){
		for(int i = 0; i < MemberNames.size();++i){
			if(MemberNames.get(i).compareTo(RemoveUsername)==0){
				MemberNames.remove(i);
				MemberSockets.remove(i);
				return true;
			}
		}
		return false;
	}
	//Display the names of all the clients in a room
	public void DisplayUsers(PrintWriter OUT){
		for(int i = 0; i < MemberNames.size();++i){
			OUT.println(MemberNames.get(i));
		}
	}
}
