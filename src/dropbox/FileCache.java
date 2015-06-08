package dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;

public class FileCache {

	public String root;
	private File directory;

	public FileCache(String filename) throws IOException {
		root = "C:/dropbox/" + filename;
		directory = new File(root);
		directory.mkdirs();
		directory.createNewFile();

		System.out.println(directory.getAbsolutePath());
	}

	public File[] getFiles() {

		return directory.listFiles();

	}

	public File findFile(String filename) throws FileNotFoundException {
		for (File file : directory.listFiles()) {
			if (file.getName().equals(filename)) {
				return file;
			}
		}
		throw new FileNotFoundException();
	}

	public boolean isFile(String filename) {
		for (File file : directory.listFiles()) {
			if (file.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}

	public void upload(Chunk chunk, long lastModified) throws IOException {
		System.out.println("uploading chunk from: " + chunk.getFilename());
		File file;
		if (!isFile(chunk.getFilename())) {
			file = new File(root + File.separator + chunk.getFilename());
			file.getParentFile().mkdirs();
			file.createNewFile();
		} else {
			file = findFile(chunk.getFilename());
		}
		RandomAccessFile rafile = new RandomAccessFile(file, "rw");
		byte[] bytes = chunk.getBytes();
		rafile.seek(chunk.getStart());
		rafile.write(bytes, 0, bytes.length);

		rafile.close();
		
		file.setLastModified(lastModified);
		// TODO return date?? fix
		// TODO file out of memory

	}

	// TODO deal with if not enough bytes file is shorter than request
	public Chunk getChunk(String filename, int start, int length)
			throws MalformedURLException, IOException {
		System.out.println("get chunk from: " + filename);
		File file = findFile(filename);
		RandomAccessFile rafile = new RandomAccessFile(file, "rw");
		rafile.seek(start);
		byte[] bytes = new byte[length];
		rafile.read(bytes, 0, length);
		Chunk aChunk = new Chunk(filename, bytes, start);
		rafile.close();
		return aChunk;

	}

	public long getLastModified(String filename) {
		File[] folder = directory.listFiles();
		for (File file : folder) {
			if (file.getName().equals(filename)) {
				return file.lastModified();
			}
		}
		return 0;
	}

}
