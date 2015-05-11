package dropbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class WriterThread extends Thread {

	private Map<String, Socket> queue;

	public WriterThread(Map<String, Socket> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {

		while (true) {

			synchronized (queue) {
				Iterator<String> iter = queue.keySet().iterator();
				while (iter.hasNext()) {
					String string;
					string = iter.next();
					Socket s = queue.get(string);
					StringTokenizer token = new StringTokenizer(string);
					String cmd = token.nextToken();
					switch (cmd) {
					case "UPLOAD": {
						break;
					}
					case "DOWNLOAD": {
						break;
					}
					case "SYNC": {
						break;
					}
					case "LOGIN": {
						break;
					}
					}
				
				}
			}
		}
	}

	public void writeMessage(Socket s, String msg) throws IOException {
		OutputStream stream = s.getOutputStream();
		PrintWriter write = new PrintWriter(stream);
		write.println(msg);
		write.flush();
	}
}
