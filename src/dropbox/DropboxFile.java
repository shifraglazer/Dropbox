package dropbox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;

public class DropboxFile extends RandomAccessFile {

	private String username;
	private String filename;
	private int size;
	private String ext;

	private Date dateModified;
	GregorianCalendar cal;

	public DropboxFile(String filename, int size)
			throws IOException {
		super(filename, "rw");
		setLength(size);
		this.username = username;
		this.filename = filename;
		this.size = size;
		cal = new GregorianCalendar();
		this.dateModified = cal.getTime();
	}
	public DropboxFile(File file) throws IOException{
		super(file,"rw");
		this.filename = file.getName();
	
		cal = new GregorianCalendar();
		this.dateModified = cal.getTime();
	}

	public String getFilename() {
		return filename;
	}

	public String getUsername() {
		return username;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Date upload(Chunk chunk) throws IOException,
			FileOutOfMemoryException {
		byte[] bytes = chunk.getBytes();
		if (size > bytes.length + chunk.getStart()) {
			seek(chunk.getStart());
			write(bytes, chunk.getStart(), bytes.length);
			dateModified = cal.getTime();
			return dateModified;
		}

		else {
			throw new FileOutOfMemoryException();
		}
	}

	// TODO deal with if not enough bytes file is shorter than request
	public Chunk getChunk(int start, int length) throws MalformedURLException,
			IOException {
		seek(start);
		byte[] bytes = new byte[length];
		read(bytes, start, length);
		Chunk aChunk = new Chunk(filename, bytes, start);
		dateModified = cal.getTime();
		return aChunk;

	}
}
