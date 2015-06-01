package dropbox;

import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkCommand extends Command {

	private String filename;
	private int offset;
	private int size;
	private static final Pattern CHUNK_COMMAND = Pattern
			.compile("CHUNK \\S+\\s\\d+\\s\\d+\\s\\d+\\s[a-zA-Z0-9=-]*{0,342}");

	public ChunkCommand(Socket socket, String filename, int offset, int size) {
		super(socket);
		this.filename = filename;
		this.offset = offset;
		this.size = size;
	}

	public boolean matches(String string) {
		Matcher match = CHUNK_COMMAND.matcher(string);
		return match.matches();
	}

	@Override
	void executeCommand(World world) {

	}

}
