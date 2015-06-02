package dropbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriterThread extends Thread {

	private Map<String, Socket> queue;
	private FileCache fileCache;
	ArrayList<Socket> sockets;
	ArrayList<ServerCommand> commands;
	private Server server;
	private static final Pattern SYNC_COMMAND = Pattern
			.compile("SYNC \\S+\\s\\d+\\s\\d+");
	Matcher matcher;

	public WriterThread(Map<String, Socket> queue, FileCache fileCache,
			ArrayList<Socket> sockets, Server server) {
		this.queue = queue;
		this.fileCache = fileCache;
		this.sockets = sockets;
		commands = new ArrayList<ServerCommand>();
		DownloadCommand download = new DownloadCommand();
		ServerChunk chunk = new ServerChunk();
		commands.add(download);
		commands.add(chunk);
		this.server = server;
	}

	@Override
	public void run() {

		while (true) {

			synchronized (queue) {
				Iterator<String> iter = queue.keySet().iterator();
				while (iter.hasNext()) {
					String string;
					String command;
					string = iter.next();
					Socket socket = queue.get(string);
					for (int i = 0; i < commands.size(); i++) {
						if (commands.get(i).matches(string)) {
							try {
								command = commands.get(i)
										.executeCommand(server);
								if (command != null) {
									matcher = SYNC_COMMAND.matcher(command);
									if (matcher.matches()) {
										sync(command);
									} else {
										writeMessage(socket, command);
									}
								}

							} catch (IOException | FileOutOfMemoryException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					// TODO transfer to create servercommands for this
					/*
					 * StringTokenizer token = new StringTokenizer(string);
					 * String cmd = token.nextToken(); switch (cmd) { case
					 * "LIST": { File[] list = fileCache.getFiles();
					 * 
					 * try { writeMessage(socket, "FILES " +
					 * String.valueOf(list.length));
					 * 
					 * for (File file : list) { writeMessage(socket, "FILE " +
					 * file.getName() + " " + file.lastModified() + " " +
					 * file.length()); } } catch (IOException e) {
					 * e.printStackTrace(); } break; }
					 */
				}

			}
		}
	}

	public void sync(String command) throws IOException {
		synchronized (sockets) {
			for (Socket asocket : sockets) {
				writeMessage(asocket, command);
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
