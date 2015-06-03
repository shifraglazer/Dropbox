package dropbox;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesCommand extends ClientCommand {
	private static final Pattern FILES_COMMAND = Pattern.compile("FILES \\d+");

	public FilesCommand() {
	}

	@Override
	public boolean matches(String string) {
		Matcher match = FILES_COMMAND.matcher(string);
		return match.matches();
	}

	@Override
	void executeCommand(Client client) throws IOException {
		// TODO Auto-generated method stub

	}

}
