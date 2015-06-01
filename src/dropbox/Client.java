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

public class Client extends World implements ReaderListener {

	private Socket socket;
	private OutputStream out;
	private static final Pattern SYNC_COMMAND = Pattern.compile("SYNC");
	private static final Pattern FILE_COMMAND = Pattern.compile("FILE \\S+\\s\\d+\\s\\d+");
	private static final Pattern UPLOAD_COMMAND = Pattern.compile("");
	private static final Pattern FILES_COMMAND = Pattern.compile("FILES \\d+");

	// CHUNK_BASE64_LENGTH=(256*4)/3== 342
	private static final Pattern CHUNK_COMMAND = Pattern
			.compile("CHUNK \\S+\\s\\d+\\s\\d+\\s\\d+\\s[a-zA-Z0-9=-]*{0,342}");
	private static final int CHUNK_SIZE = 256;
	private PrintWriter write;
	private List<Command> commands;

	public Client() throws UnknownHostException, IOException {
		fileCache = new FileCache();
		commands = new ArrayList<Command>();
		DownloadCommand download = new DownloadCommand(null, null, 0, 0);
		ChunkCommand chunk = new ChunkCommand(null, null, 0, 0);
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
		Matcher files = FILES_COMMAND.matcher(line);
		Matcher chunk = CHUNK_COMMAND.matcher(line);
		Matcher upload = UPLOAD_COMMAND.matcher(line);
		Matcher file = FILE_COMMAND.matcher(line);
		Matcher sync = SYNC_COMMAND.matcher(line);
		if (files.matches()) {
			System.out.println(line);
		}
		// send out download
		else if (chunk.matches()) {
			try {
				downloadChunk(line);
			} catch (IOException | FileOutOfMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (sync.matches()) {
			syncFile(line);
		} else if (file.matches()) {
			requestUpdate(line);
		}
	}

	public void requestUpdate(String line) {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
		file = token.nextToken();
		int lastModified = Integer.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		try {
			if (lastModified > fileCache.findFile(file).lastModified()) {
				requestDownloadFile(file, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(file, size);
		}
	}

	public void syncFile(String line) {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
		file = token.nextToken();
		int lastModified = Integer.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		// TODO remove/fix file not found
		try {
			if (lastModified > fileCache.findFile(file).lastModified()) {
				requestDownloadFile(file, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(file, size);
		}

	}

	public void downloadChunk(String line) throws IOException, FileOutOfMemoryException {
		StringTokenizer token = new StringTokenizer(line);
		String chunk = token.nextToken();
		chunk = token.nextToken();
		int lastModified = Integer.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		int offset = Integer.valueOf(token.nextToken());
		String base64 = token.nextToken();
		byte[] decoded = Base64.getDecoder().decode(base64);
		fileCache.addChunk(new Chunk(chunk, decoded, offset));

	}

	public void requestDownloadFile(String filename, int size) {
		int downloadedSize = 0;
		while (downloadedSize < size) {
			downloadChunkMsg(filename, downloadedSize, CHUNK_SIZE);
		}
	}

	public void downloadChunkMsg(String filename, int downloadedSize, int chunkSize) {
		writeMessage("DOWNLOAD " + filename + " " + downloadedSize + " " + chunkSize);

	}

	@Override
	public void onCloseSocket(Socket socket) {

	}

}
