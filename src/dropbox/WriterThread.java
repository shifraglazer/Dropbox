package dropbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class WriterThread extends Thread {

	private ConcurrentHashMap<String, Socket> queue;
	private FileCache fileCache;
	ArrayList<Socket> sockets;
	ArrayList<ServerCommand> commands;

	public WriterThread(ConcurrentHashMap<String, Socket> queue,
			FileCache fileCache, ArrayList<Socket> sockets, Server server) {
		this.queue = queue;
		this.fileCache = fileCache;
		this.sockets = sockets;
		commands = new ArrayList<ServerCommand>();
		DownloadCommand download = new DownloadCommand();
		ServerChunk chunk = new ServerChunk();
		ListCommand list = new ListCommand();
		commands.add(download);
		commands.add(chunk);
		commands.add(list);

	}

	@Override
	public void run() {

		while (true) {
			Iterator<String> iter = queue.keySet().iterator();
			while (iter.hasNext()) {
				String string;
				string = iter.next();
				Socket socket = queue.get(string);
				System.out.println("writer: " + string);
				for (int i = 0; i < commands.size(); i++) {
					if (commands.get(i).matches(string)) {
						try {
							commands.get(i).executeCommand(fileCache, socket,
									sockets);
							queue.remove(string);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}
		}
	}

}
