package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class FileCache {

	public List<DropboxFolder> folders;
	public static final String ROOT = "dropbox/";
	private File directory;

	public FileCache() {
		directory = new File(ROOT);
		directory.mkdir();
		folders = new ArrayList<DropboxFolder>();
	}

	public boolean isUsername(String username) {
		for (DropboxFolder folder : folders) {
			if (folder.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public boolean makeDir(String username) {
		if (isUsername(username)) {
			return false;
		}
		DropboxFolder file = new DropboxFolder(ROOT + "/" + username);
		folders.add(file);
		return true;
	}

	public File[] getFiles(String username) {
		for (DropboxFolder file : folders) {
			if (file.getUsername().equals(username)) {
				return file.listFiles();
			}
		}
		return null;
	}

	//client add chunk to server file
	public void addChunk(Chunk chunk) {
		
	}
	//client get chunk from server
	public Chunk getChunk(String username, String filename, int start,
			int length) throws MalformedURLException, IOException {
		File[] folder=getFiles(username);
		for(File file:folder){
			if(file.getName().equals(filename)){
				DropboxFile match=(DropboxFile) file;
				return match.getChunk(start,length);
			}
		}
		//TODO if not enough to send back send length of actual data?
		//TODO throw exception?
		return null;
	}
}
