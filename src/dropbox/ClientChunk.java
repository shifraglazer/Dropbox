package dropbox;

import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientChunk extends ClientCommand {

	private String line;
	// CHUNK_BASE64_LENGTH=(256*4)/3== 342
	private static final Pattern CHUNK_COMMAND = Pattern
			.compile("CHUNK \\S+\\s\\d+\\s\\d+\\s\\d+\\s[a-zA-Z0-9=/]*{0,}");

	public ClientChunk() {

	}

	@Override
	void executeCommand(Client client) throws IOException{
		StringTokenizer token = new StringTokenizer(line);
		String chunk = token.nextToken();
		String filename = token.nextToken();
		long lastmodified = Long.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		int offset = Integer.valueOf(token.nextToken());
		String base64 = token.nextToken();
		byte[] bytes = Base64.getDecoder().decode(base64);
		client.addChunk(new Chunk(filename, bytes, offset));
	}

	@Override
	boolean matches(String string) {
		Matcher match = CHUNK_COMMAND.matcher(string);
		this.line = string;
		return match.matches();
	}

}
