package dropbox;

import java.io.File;
import java.io.IOException;



public class ListCommand extends ServerCommand{
	
	File[] list;
	
	public ListCommand(File[] list){
		this.list = list;
	}
	
	@Override
	boolean matches(String string) {//see if matches the string
		if(string.equalsIgnoreCase("LIST")){
			return true;
		}
		return false;
	}

	@Override
	String executeCommand(Server server) throws IOException, FileOutOfMemoryException {
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
		
		return b.toString();
	}

}
