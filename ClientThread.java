import java.io.*;
import java.net.*;
import java.util.*;

//The client thread is used so that there isn't any interruption between the input and output of the program
//The ClientThread will constantly wait for input from the server. If it receives a message from the server,
//It will add it to the ArrayList located in the Client object
public class ClientThread extends Thread{
	private Socket socket;//Socket of the server
	//Constructor for the client thread
    ClientThread(Socket socket,String name){
            this.socket = socket;
    }
    //The ClientThread will wait for the server to send it a message. When it receives a message, add it the
    //ArrayList in the Client object
    public void run(){
            try{
            	    boolean connected = true;
                    String message;
                    Scanner IN = new Scanner(socket.getInputStream());
                    while(connected){
                    	    if(IN.hasNext()){
                    	    	message = IN.nextLine();
                    	    	if(Client.messages.size() > 20)
                    	    		Client.messages.remove(0);
                    	    	Client.messages.add(message);
                    	    }
                    	    else{
                    	    	IN.close();
                    	    	connected = false;
                    	    }
                    }
                    socket.close();
            }catch(IOException e){
               e.printStackTrace();
             }
    }
}
