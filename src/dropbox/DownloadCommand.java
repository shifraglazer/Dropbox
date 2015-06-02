package dropbox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadCommand extends ServerCommand {

	private String line;
	private static final Pattern PATTERN = Pattern.compile("");

	public boolean matches(String string) {
		Matcher match = PATTERN.matcher(string);
		line = string;
		return match.matches();
	}

	@Override
	public String executeCommand(Server server) throws MalformedURLException, IOException {
		StringTokenizer token = new StringTokenizer(line);
		String cmd = token.nextToken();
		String filename = token.nextToken();
		int start = Integer.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		Chunk chunk;
		long lastmodified = server.getFileLastModified(filename);

		chunk = server.getChunk(filename, start, size);

		return "CHUNK " + filename + " " + lastmodified + " " + size + " "
				+ start + " "
				+ Base64.getEncoder().encodeToString(chunk.getBytes());
	}

}
