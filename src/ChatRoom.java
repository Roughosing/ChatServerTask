import java.util.*;

public class ChatRoom {

	private int roomRef;
	private String roomName;
	private Map<Integer, Client> members;

	ChatRoom(int ref, String name) {
		this.roomRef = ref;
		this.roomName = name;
		members = new HashMap<Integer, Client>();
	}

	public int getId() {
		return roomRef;
	}

	public String getName() {
		return roomName;
	}

	public void addMember(Client client, int id) {
		members.put(id, client);
	}

	public void removeMember(int id) {
		members.remove(id);
	}
	
	public Map<Integer, Client> getMembers(){
		return this.members;
	}
}
