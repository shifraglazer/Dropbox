package dropbox;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyncCommand extends ClientCommand {

	private String line;
	private static final Pattern SYNC_COMMAND = Pattern
			.compile("SYNC \\S+\\s\\d+\\s\\d+");

	public SyncCommand() {
	}

	@Override
	public boolean matches(String string) {
		Matcher match = SYNC_COMMAND.matcher(string);
		line = string;
		return match.matches();

	}

	@Override
	void executeCommand(Client client) throws IOException {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
		file = token.nextToken();
		int lastModified = Integer.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());
		client.syncFile(file, lastModified, size);
	}

}
