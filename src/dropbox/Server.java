package dropbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ReaderListener {

	private Socket socket;
	private ConcurrentHashMap<String, Socket> queue;
	private ArrayList<Socket> sockets;
	private ProcessServerCommands write;
	private FileCache fileCache;

	public Server(String filename) throws IOException {
		fileCache = new FileCache(filename);
		queue = new ConcurrentHashMap<String, Socket>();
		sockets = new ArrayList<Socket>();

		write = new ProcessServerCommands(queue, fileCache, sockets, this);
		write.start();
		try {
			ServerSocket serverSocket = new ServerSocket(8181);
			while (true) {
				socket = serverSocket.accept();
				synchronized (sockets) {
					sockets.add(socket);
					System.out.println("client added");
				}

				ReaderThread thread = new ReaderThread(socket, this);
				thread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLineRead(Socket socket, String line) {
		queue.put(line, socket);
	}

	@Override
	public void onCloseSocket(Socket socket) {
		System.out.println("Thank you for using Dropbox!");
	}

	public static void main(String args[]) {
		try {

			Server server = new Server("server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
