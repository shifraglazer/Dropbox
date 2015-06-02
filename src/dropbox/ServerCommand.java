package dropbox;

import java.io.IOException;

public abstract class ServerCommand {


	abstract boolean matches(String string);

	abstract String executeCommand(Server server) throws IOException,
			FileOutOfMemoryException;

}
