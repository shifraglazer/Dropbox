package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadCommand extends Command {

	private String filename;
	private int offset;
	private int chunksize;
	private static final Pattern PATTERN = Pattern.compile("");

	public DownloadCommand(Socket socket, String filename, int offset, int chunksize) {
		super(socket);

		this.filename = filename;
		this.offset = offset;
		this.chunksize = chunksize;

	}

	public boolean matches(String string) {
		Matcher match = PATTERN.matcher(string);
		return match.matches();
	}

	@Override
	public void executeCommand(World world) {
		ChunkCommand chunk = world.getChunkCommand(filename, offset, chunksize);
		world.writeCommand(chunk);
		// writeMessage(socket, Base64.getEncoder().encodeToString(chunk.));
	}

}
