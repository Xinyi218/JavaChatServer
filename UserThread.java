import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

//Copyright to  Nam Ha Minh

//thread handles connection for each connected client, server can handle multiple clients at the same time 
public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server){
        this.socket = socket;
        this.server = server;
    }

    public void run(){
        try{
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             writer = new PrintWriter(output, true);
             printUsers();
             String userName = reader.readLine();
             server.addUserName(userName);
             String serverMessage = "New user connected: " + userName;
             server.boardcast(serverMessage, this);
             String clientMessage;
             do{
                 clientMessage = reader.readLine();
                 serverMessage = "[" + userName + "]: " + clientMessage;
                 server.boardcast(serverMessage, this);
             }while(!clientMessage.equals("bye"));
             server.removeUser(userName, this);
             socket.close();
             serverMessage = userName = "has quitted";
             server.boardcast(serverMessage, this);
        }catch(IOException ex){
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    void printUsers(){
        if(server.hasUsers()){
            writer.println("Connected users: " +server.getUserNames());
        }else {
            writer.println("No other users connected");
        }
    }
    void sendMessage(String message){
        writer.println(message);
    }
}