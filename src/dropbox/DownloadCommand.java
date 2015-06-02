package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadCommand extends ServerCommand {

	private String line;
	private static final Pattern PATTERN = Pattern.compile("");

	public DownloadCommand(Socket socket) {
		super(socket);
	}

	public boolean matches(String string) {
		Matcher match = PATTERN.matcher(string);
		line=string;
		return match.matches();
	}

	@Override
	public void executeCommand(Server server) {
		ChunkCommand chunk = server.getChunkCommand(filename, offset, chunksize);
		//server.writeCommand(chunk);
		// writeMessage(socket, Base64.getEncoder().encodeToString(chunk.));
	}

}
