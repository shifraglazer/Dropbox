package dropbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements ReaderListener {

	private Socket socket;
	private OutputStream out;

	private static final Pattern UPLOAD_COMMAND = Pattern.compile("");
	private static final int CHUNK_SIZE = 256;
	private PrintWriter write;
	private List<ClientCommand> commands;
	private FileCache fileCache;

	public Client() throws UnknownHostException, IOException {
		fileCache = new FileCache();
		commands = new ArrayList<ClientCommand>();
		// DownloadCommand download = new DownloadCommand(null, null, 0, 0);
		ClientChunk chunk = new ClientChunk();
		SyncCommand sync = new SyncCommand();
		FileCommand file = new FileCommand();
		FilesCommand files = new FilesCommand();
		commands.add(files);
		commands.add(chunk);
		commands.add(sync);
		commands.add(file);

		socket = new Socket("localhost", 6003);
		new ReaderThread(socket, this).start();
		out = socket.getOutputStream();
		write = new PrintWriter(out);
	}

	public void requestFiles() {
		writeMessage("FILES");
	}

	public void writeMessage(String message) {
		write.println(message);
		write.flush();
	}

	@Override
	public void onLineRead(Socket socket, String line) {
		String string = line;

		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).matches(line)) {
				try {
					commands.get(i).executeCommand(this);
				} catch (IOException | FileOutOfMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void requestUpdate(String filename, long lastmodified, int size) {

		try {
			if (lastmodified > fileCache.findFile(filename).lastModified()) {
				requestDownloadFile(filename, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(filename, size);
		}
	}

	public void syncFile(String filename, long lastmodified, int size) {

		// TODO remove/fix file not found
		try {
			if (lastmodified > fileCache.findFile(filename).lastModified()) {
				requestDownloadFile(filename, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(filename, size);
		}

	}

	public void addChunk(Chunk chunk) throws IOException,
			FileOutOfMemoryException {
		fileCache.addChunk(chunk);
	}

	public void requestDownloadFile(String filename, int size) {
		int downloadedSize = 0;
		while (downloadedSize < size) {
			downloadChunkMsg(filename, downloadedSize, CHUNK_SIZE);
		}
	}

	public void downloadChunkMsg(String filename, int downloadedSize,
			int chunkSize) {
		writeMessage("DOWNLOAD " + filename + " " + downloadedSize + " "
				+ chunkSize);

	}

	@Override
	public void onCloseSocket(Socket socket) {

	}

}
