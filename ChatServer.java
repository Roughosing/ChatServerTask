
import java.io.*;
import java.util.*;
import java.net.*;

public class ChatServer {

	private static String serverIP;
	private static int serverPort;
	private static List<Connection> members;
	private static List<ChatRoom> chatRooms;
	private static int index;

	public static void main(String[] args){

		System.out.println("Server is Running");
		ServerSocket socket;
		chatRooms = new ArrayList<ChatRoom>();
		members = new ArrayList<Connection>();
		try {
			socket = new ServerSocket(22);
			setServerIP(InetAddress.getLocalHost().getHostAddress());
			setPort(socket.getLocalPort());
			System.out.println("My IP : " + InetAddress.getLocalHost().getHostAddress());
			System.out.println("My Port : " + socket.getLocalPort());
			while(true){
				Socket connect = socket.accept();
				System.out.println("Client '" + connect.getInetAddress() + "' connected\n");
				Connection connection = new Connection(connect);
				connection.start();
				members.add(connection);
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
