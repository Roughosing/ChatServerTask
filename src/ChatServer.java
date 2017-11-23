
import java.io.*;
import java.util.*;
import java.net.*;

public class ChatServer {

	private static String serverIP;
	private static int serverPort;
	private static List<Client> members;
	private static List<ChatRoom> chatRooms;
	public static int index;

	public static void main(String[] args){

		System.out.println("Server is Running");
		ServerSocket socket;
		chatRooms = new ArrayList<ChatRoom>();
		members = new ArrayList<Client>();
		int port = Integer.parseInt(args[0]);
		try {
			socket = new ServerSocket(port);
			setServerIP(InetAddress.getLocalHost().getHostAddress());
			setPort(socket.getLocalPort());
			System.out.println("My IP : " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("My Port : " + socket.getLocalPort());
			while(true){
				Socket connect = socket.accept();
				System.out.println("Client '" + connect.getInetAddress() + "' connected");
				Client newClient = new Client(connect);
				newClient.start();
				members.add(newClient); 	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setServerIP(String ip) {
		serverIP = ip;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static void setPort(int port) {
		serverPort = port;
	}

	public static int getPort() {
		return serverPort;
	}
	
	public static List<Client> getMembers(){
		return members;
	}

	public static ChatRoom findRoom(String name){
		ChatRoom roomFound = null;
		if(chatRooms.isEmpty())
			return null;
		for(ChatRoom room : chatRooms){
			if(room.getName().equals(name))
				roomFound = room;
		}
		return roomFound;
	}

	public static ChatRoom createRoom(String name){
		index++;
		ChatRoom newRoom = new ChatRoom(index, name.trim());
		chatRooms.add(newRoom);
		return newRoom;
	}
}
