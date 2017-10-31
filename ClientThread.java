
import java.io.*;
import java.net.*;

public class ClientThread extends Thread{

	private Socket socket;
	private String message;

	ClientThread (Socket socket) {
		this.socket = socket;
	}

	public void run(){
		BufferedWriter output;
		try {
			output = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
			while(true){
				sleep(0);
				if (message == null)
					continue;
				output.write(message + "\n");
				output.flush();
				//System.out.println(message);
				message = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
