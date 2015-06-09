package dropbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public abstract class ServerCommand {

	abstract boolean matches(String string);

	abstract void executeCommand(FileCache fileCache, Socket socket, ArrayList<Socket> sockets) throws IOException;

	public void writeMessage(Socket s, String msg) throws IOException {
		System.out.println("sever sending cmd: "+ msg);
		OutputStream stream = s.getOutputStream();
		PrintWriter write = new PrintWriter(stream);
		write.println(msg);
		write.flush();
	}
}
