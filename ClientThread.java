
import java.io.*;
import java.net.*;

public class ClientThread extends Thread{

	private Socket socket;
	private BufferedWriter output;
	private String message;
	
	ClientThread (Socket socket) {
		this.message = null;
		this.socket = socket;
	}
	 	
	public void run(){
		try {
			 output = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
			 while(true){
				sleep(0);
				if (message == null)
					continue;
				 output.write(message);
				 output.flush();
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
