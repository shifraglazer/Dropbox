package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.Socket;



public class ListCommand extends ServerCommand{
	
	
	
	
	@Override
	boolean matches(String string) {//see if matches the string
		return "LIST".equalsIgnoreCase(string);
	}

	@Override
	void executeCommand(FileCache fileCache, Socket socket, Socket[] sockets) throws IOException, FileOutOfMemoryException {
			
		File[] list = fileCache.getFiles();
		
		StringBuilder b = new StringBuilder();
		b.append("FILES ");
		b.append(list.length);
		b.append("\n");
		
		for (File file : list) {			
			b.append("FILE ");
			b.append(file.getName());
			b.append(" ");
			b.append(file.lastModified());
			b.append(" ");
			b.append(file.length());
			b.append("\n");
		}
		
		writeMessage(socket,  b.toString());
	}

}
