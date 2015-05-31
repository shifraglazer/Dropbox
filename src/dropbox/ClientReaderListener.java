package dropbox;

import java.net.Socket;

public interface ClientReaderListener {

	void onLineRead(String line);

	void onCloseSocket(Socket socket);
}
