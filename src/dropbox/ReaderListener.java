package dropbox;

import java.net.Socket;

public interface ReaderListener {

	void onLineRead(Socket socket, String line);

	void onCloseSocket(Socket socket);

}
