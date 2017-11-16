import java.util.*;
import java.io.*;
import java.net.*;

public class Client extends Thread {

	private Socket socket;
	private String clientName;
	private int id;
	private BufferedWriter output;
	private BufferedReader reader;
	private Map<Integer, ChatRoom> joinedRooms;

	Client(Socket socket, BufferedReader reader, BufferedWriter output) {
		this.socket = socket;
		this.output = output;
		this.reader = reader;
		joinedRooms = new HashMap<Integer, ChatRoom>();
	}

	public void run(){
		try {
			while(true){
				String[][] arr = new String[4][2];
				int type = -1, index = 0;
				while (type == -1 && index<4){
					String[] input = (String[]) reader.readLine().split(" ", 2);						
					arr[index] = input;
					type = decodeMessage(arr);
					index++;
				}
				switch (type) {
				case 1:
					// join/create chat room
					ChatRoom roomJoin = ChatServer.findRoom(arr[0][1].trim());
					id = ChatServer.getMembers().indexOf(this);
					if (roomJoin == null)
						roomJoin = ChatServer.createRoom(arr[0][1].trim());
					roomJoin.addMember(this, id);
					joinedRooms.put(roomJoin.getId(), roomJoin);
					clientName = arr[3][1].trim();
					
					sendJoinedMessage(roomJoin, index);
					break;
				case 2:
					// leave room
					int id = -1;
					ChatRoom roomLeave = null;
					for (int key : joinedRooms.keySet()) {
						ChatRoom room = joinedRooms.get(key);
						if (room.getId() == Integer.parseInt(arr[0][1].trim())) {
							id = key;
							roomLeave = room;
						}
					}
					joinedRooms.remove(this.id);
					roomLeave.removeMember(this.id);
					sendLeaveMessage(roomLeave, id);
					break;
				case 3:
					// disconnect clients, close socket
					for (int key : joinedRooms.keySet()) {
						joinedRooms.get(key).removeMember(key);
					}
					socket.close();
					break;
				case 4:
					// cast message
					ChatRoom myChatRoom = joinedRooms.get(Integer.parseInt(arr[0][1].trim()));
					String messages = castMessage(arr);
					for(Client Client : myChatRoom.getMembers().values()){
						Client.output.write(messages);
					}
					break;
				case 5:
					sendHELO();
					break;
				case 6:
					socket.close();
					break;
				default:
					// error
					sendErrorMessage();
					break;
				}
				output.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendHELO() throws IOException{
		String HELOtext = "HELO BASE_TEST" 
			+ "\nIP: " + InetAddress.getLocalHost().getHostAddress() 
			+ "\nPort: " + socket.getLocalPort()  
			+ "\nStudentID: 14316190";
		output.write(HELOtext);
	}

	private void sendErrorMessage() throws IOException {
		String outputMessage = "ERROR_CODE: 0"
			+ "\nERROR_DESCRIPTION: Error Occured\n\n";
		output.write(outputMessage);
	}

	private void sendLeaveMessage(ChatRoom roomLeave, int leaveId) throws IOException {
		String outputMessage = "LEFT_CHATROOM: " + roomLeave.getId() 
			+ "\nJOIN_ID: " + id
			+ "\nCHAT: " + roomLeave.getId()
			+ "\nCLIENT_NAME: " + clientName
			+ "\nMESSAGE: " + clientName + " has left this chatroom.\n\n";
		output.write(outputMessage);
	}

	private void sendJoinedMessage(ChatRoom roomJoin, int joinId) throws IOException {
		String outputMessage = "JOINED_CHATROOM: " + roomJoin.getName() 
			+ "\nSERVER_IP: " + ChatServer.getServerIP()
			+ "\nPORT: " + ChatServer.getPort() 
			+ "\nROOM_REF: " + roomJoin.getId() 
			+ "\nJOIN_ID: " + id
			+ "\nCHAT: " + roomJoin.getId()
			+ "\nCLIENT_NAME: " + clientName 
			+ "\nMESSAGE: " + clientName + " has joined this chatroom.\n\n";
		output.write(outputMessage);
	}

	private String castMessage(String[][] arr) {
		String outputMessage = "CHAT: " + arr[0][1] 
			+ "\nCLIENT_NAME: " + clientName
			+ "\nMESSAGE: " + arr[3][1]  + "\n\n";
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
			} else if(arr[0][0].trim().equals("HELO")) {
				return 5;
			} else if (arr[0][0].trim().equals("KILL_SERVICE")){
				return 6;
			}else{
				throw new Exception();
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
