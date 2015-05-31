package dropbox;

import java.net.Socket;



public abstract class Command {

	protected Socket socket;
	public Command(Socket socket){
		this.socket=socket;
	}
	
	abstract void executeCommand(World world);
}
