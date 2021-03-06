package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadCommand extends ServerCommand {

	private Socket socket;
	private String line;
	private static final Pattern PATTERN = Pattern.compile("DOWNLOAD \\S+");
	private static final int CHUNK_SIZE = 512;

	public boolean matches(String string) {
		Matcher match = PATTERN.matcher(string);
		line = string;
		return match.matches();
	}

	@Override
	void executeCommand(FileCache fileCache, Socket socket, ArrayList<Socket> sockets) throws IOException {
		this.socket = socket;
		StringTokenizer token = new StringTokenizer(line);
		String cmd = token.nextToken();
		String filename = token.nextToken();
		File file = fileCache.findFile(filename);
		int offset = 0;
		int length;
		while (offset < file.length()) {

			if (file.length() - offset < CHUNK_SIZE) {
				length = (int) (file.length() - offset);
			} 
			else {
				length = CHUNK_SIZE;
			}

			System.out.println("getting chunk sever..");

			Chunk chunk = fileCache.getChunk(filename, offset, length);
			writeMessage(socket, "CHUNK " + filename + " " + file.lastModified() + " " + file.length() + " " + offset + " " + Base64.getEncoder().encodeToString(chunk.getBytes()));
			offset += length;
		}

	}

}
