import java.util.*;

public class ChatRoom {

	private int roomRef;
	private String roomName;
	private Map<Integer, Connection> members;
	private static int index;

	ChatRoom(int ref, String name) {
		this.roomRef = ref;
		this.roomName = name;
		members = new HashMap<Integer, Connection>();
	}

	public int getId() {
		return roomRef;
	}

	public String getName() {
		return roomName;
	}

	public int addMember(Connection newConnection) {
		index++;
		members.put(index, newConnection);
		return index;
	}

	public void removeMember(int id) {
		members.remove(id);
	}
	
	public Map<Integer, Connection> getMembers(){
		return this.members;
	}
	
}
