package dropbox;

import java.io.IOException;
import java.net.Socket;

public abstract class ServerCommand {

	protected Socket socket;

	public ServerCommand(Socket socket) {
		this.socket = socket;
	}

	abstract boolean matches(String string);

	abstract void executeCommand(Server server) throws IOException,
			FileOutOfMemoryException;

}
