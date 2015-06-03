package dropbox;

import java.io.IOException;

public abstract class ClientCommand {

	
	abstract boolean matches(String string);

	abstract void executeCommand(Client client) throws IOException,
			FileOutOfMemoryException;

	
}
