package dropbox;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerChunk extends ServerCommand {

	// CHUNK_BASE64_LENGTH=(256*4)/3== 342
	private static final Pattern CHUNK_COMMAND = Pattern
			.compile("CHUNK \\S+\\s\\d+\\s\\d+\\s\\d+\\s[a-zA-Z0-9=-]*{0,}");

	private String line;

	@Override
	void executeCommand(FileCache fileCache, Socket socket,
			ArrayList<Socket> sockets) throws IOException {
		StringTokenizer token = new StringTokenizer(line);
		String chunk = token.nextToken();
		String filename = token.nextToken();
		// TODO is last modified correct or need updated?
		long lastmodified = Long.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		int offset = Integer.valueOf(token.nextToken());
		String base64 = token.nextToken();
		byte[] bytes = Base64.getDecoder().decode(base64);
		fileCache.addChunk(new Chunk(filename, bytes, offset));
		if (offset + bytes.length == size) {
			for (Socket asocket : sockets) {
				writeMessage(asocket, "SYNC " + filename + " " + lastmodified
						+ " " + size);
			}
		}

	}

	@Override
	boolean matches(String string) {
		Matcher match = CHUNK_COMMAND.matcher(string);
		line = string;
		return match.matches();
	}

}
