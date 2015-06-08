package dropbox;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesCommand extends ClientCommand {
	
	private String line;
	private static final Pattern FILES_COMMAND = Pattern.compile("FILES \\d+");

	public FilesCommand() {
	}

	@Override
	public boolean matches(String string) {
		Matcher match = FILES_COMMAND.matcher(string);
		line = string;
		return match.matches();
	}

	@Override
	void executeCommand(Client client) throws IOException {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
	
		int size = Integer.valueOf(token.nextToken());
		
		client.filesCmd(size);

	}

}
