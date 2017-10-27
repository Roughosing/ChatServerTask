import java.util.*;
import java.io.*;
import java.net.*;

public class Connection extends Thread{

	private Socket socket;
	private ClientThread writer; 
	private BufferedReader reader;
	private Map<Integer, ChatRoom> myChatRooms;

	Connection(Socket socket) {
		this.socket = socket;
		writer = new ClientThread(this.socket);
		writer.start();
		myChatRooms = new HashMap<Integer, ChatRoom>();
	}

	public void run(){
		try {
			reader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			while(true){
				String[][] arr = new String[4][2];
				int type = -1, index = 0;
				while (type == -1 || index<4){
					String[] input = (String[]) reader.readLine().split(" ");
					arr[index] = input;
					type = decodeMessage(arr);
					index++;
				}
				switch (type) {
				case 1:
					// join/create chat room
					ChatRoom roomJoin = ChatServer.findRoom(arr[0][1].trim());
					if (roomJoin == null)
						roomJoin = ChatServer.createRoom(arr[0][1].trim());
					int joinId = roomJoin.addMember(this);
					myChatRooms.put(joinId, roomJoin);
					sendJoinedMessage(roomJoin, joinId);
					break;

				case 2:
					// leave room
					int id = -1;
					ChatRoom roomLeave = null;
					for (int key : myChatRooms.keySet()) {
						ChatRoom room = myChatRooms.get(key);
						if (room.getId() == Integer.parseInt(arr[0][1].trim())) {
							id = key;
							roomLeave = room;
							break;
						}
					}
					myChatRooms.remove(id);
					roomLeave.removeMember(id);
					sendLeaveMessage(roomLeave, id);
					break;
				case 3:
					// disconnect clients, close socket
					for (int key : myChatRooms.keySet()) {
						myChatRooms.get(key).removeMember(key);
					}
					socket.close();
					break;
				case 4:
					// cast all messages
					ChatRoom myChatRoom = myChatRooms.get(Integer.parseInt(arr[0][1].trim()));
					String messages = castMessages(arr);
					for(Connection connections : myChatRoom.getMembers().values()){
						connections.writer.setMessage(messages);
					}
					arr = null;
					break;
				case 5:
					sendHELO();
					break;
				default:
					// error
					sendErrorMessage();
					break;
				}

				arr = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sendHELO() throws UnknownHostException{
		String HELOtext = "HELO BASE_TEST\r\r\n" +
				"IP: " + InetAddress.getLocalHost().getHostAddress() + "\r\r\n" +
				"Port: " + socket.getLocalPort() + "\r\n" +
				"Student ID: 14316190";
		writer.setMessage(HELOtext);
	}

	private void sendErrorMessage() {
		String outputMessage = "ERROR_CODE: 0\r\nERROR_DESCRIPTION: Error Occured\r\n";
		writer.setMessage(outputMessage);
	}

	private void sendLeaveMessage(ChatRoom roomLeave, int joinId) throws IOException {
		String outputMessage = "LEFT_CHATROOM: " + roomLeave.getId() 
		+ "\r\nJOIN_ID: " + joinId + "\r\n";
		writer.setMessage(outputMessage);
	}

	private void sendJoinedMessage(ChatRoom roomJoin, int joinId) throws IOException {
		String outputMessage = "JOINED_CHATROOM: " + roomJoin.getName() 
		+ "\r\nSERVER_IP: " + ChatServer.getServerIP()
		+ "\r\nPORT: " + ChatServer.getPort() 
		+ "\r\nROOM_REF: " + roomJoin.getId() 
		+ "\r\nJOIN_ID: " + joinId + "\r\n";
		writer.setMessage(outputMessage);
	}

	private String castMessages(String[][] arr) {
		String outputMessage = "CHAT: " + arr[0][1] 
				+ "\r\nCLIENT_NAME: " + arr[2][1]
						+ "\r\nMESSAGE: " + arr[3][1]  + "\r\n";
		return outputMessage;
	}

	private static int decodeMessage(String[][] arr) {
		try {
			if (arr[0][0].trim().equals("JOIN_CHATROOM:")) {
				if (!arr[1][0].trim().equals("CLIENT_IP:") 
						|| !arr[2][0].trim().equals("PORT:")
						|| !arr[3][0].trim().equals("CLIENT_NAME:")) {
					return -1;
				}
				return 1;
			} else if (arr[0][0].trim().equals("LEAVE_CHATROOM:")) {
				if (!arr[1][0].trim().equals("JOIN_ID:") 
						|| !arr[2][0].trim().equals("CLIENT_NAME:")) {
					return -1;
				}
				return 2;
			} else if (arr[0][0].trim().equals("DISCONNECT:")) {
				if (!arr[1][0].trim().equals("PORT:") 
						|| !arr[2][0].trim().equals("CLIENT_NAME:")) {
					return -1;
				}
				return 3;
			} else if (arr[0][0].trim().equals("CHAT:")) {
				if (!arr[1][0].trim().equals("JOIN_ID:") 
						|| !arr[2][0].trim().equals("CLIENT_NAME:")
						|| !arr[3][0].trim().equals("MESSAGE:")) {
					return -1;
				}
				return 4;
			} else if(arr[0][0].trim().equals("HELO BASE_TEST")) {
				return 5;
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			return -1;
		}
	}

}
