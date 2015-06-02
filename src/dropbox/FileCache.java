package dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
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

	public File[] getFiles() {
		File file = new File(ROOT);
		return file.listFiles();

	}

	/*
	 * public File[] getFiles(String username) { for (DropboxFolder file :
	 * folders) { if (file.getUsername().equals(username)) { return
	 * file.listFiles(); } } return null; }
	 */

	// client add chunk to server file
	public Date addChunk(Chunk chunk) throws IOException, FileOutOfMemoryException {
		File[] folder = directory.listFiles();
		for (File file : folder) {
			if (file.getName().equals(chunk.getFilename())) {
				DropboxFile match = new DropboxFile(file);
				match.close();
				return match.upload(chunk);

			}
		}

		DropboxFile file = new DropboxFile(chunk.getFilename(), chunk.getBytes().length);
		file.close();
		return file.upload(chunk);
	}

	// client get chunk from server
	public Chunk getChunk(String filename, int start, int length) throws MalformedURLException, IOException {
		File[] folder = directory.listFiles();
		for (File file : folder) {
			if (file.getName().equals(filename)) {
				DropboxFile match = new DropboxFile(file);
				match.close();
				return match.getChunk(start, length);
			}
		}
		// TODO if not enough to send back send length of actual data?
		// TODO throw exception?
		return null;
	}

	public File findFile(String filename) throws FileNotFoundException {
		for (File file : directory.listFiles()) {
			if (file.getName().equals(filename)) {
				return file;
			}
		}
		throw new FileNotFoundException();
	}

	public long getLastModified(String filename) throws FileNotFoundException {
		File[] folder = directory.listFiles();
		for (File file : folder) {
			if (file.getName().equals(filename)) {
			return file.lastModified();
			}
		}
		throw new FileNotFoundException();
	}

}
