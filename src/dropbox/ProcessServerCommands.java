package dropbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessServerCommands extends Thread {

	private ConcurrentHashMap<String, Socket> listOfCmds;
	private FileCache fileCache;
	ArrayList<Socket> sockets;
	ArrayList<ServerCommand> commands;

	public ProcessServerCommands(ConcurrentHashMap<String, Socket> queue,
			FileCache fileCache, ArrayList<Socket> sockets, Server server) {
		this.listOfCmds = queue;
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
			Iterator<String> iter = listOfCmds.keySet().iterator();
			while (iter.hasNext()) {
				String string;
				string = iter.next();
				Socket socket = listOfCmds.get(string);
				System.out.println("Server read: " + string);
				for (int i = 0; i < commands.size(); i++) {
					if (commands.get(i).matches(string)) {
						try {
							commands.get(i).executeCommand(fileCache, socket,
									sockets);
							listOfCmds.remove(string);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}

			}
		}
	}

}
