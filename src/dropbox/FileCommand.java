package dropbox;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCommand extends ClientCommand{

	private String line;
	private static final Pattern FILE_COMMAND = Pattern.compile("FILE \\S+\\s\\d+\\s\\d+");


	@Override
	public boolean matches(String string) {
		Matcher match = FILE_COMMAND.matcher(string);
		line = string;
		return match.matches();
	}
	@Override
	void executeCommand(Client client) throws IOException {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
		file = token.nextToken();
		long lastModified = Long.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());

		client.uploadFile(line);
		client.requestUpdate(file, lastModified, size);
	}

}
