package dropbox;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;

public class DropboxFile extends RandomAccessFile {

	private String filename;
	private int size;
	private String ext;

	private Date dateModified;
	GregorianCalendar cal;

	public DropboxFile(File directory,String filename, int size) throws IOException {
		super(filename, "rw");
		File file=new File(directory.getAbsolutePath()+"//"+filename);
		file.mkdirs();
		setLength(size);
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

	public Date upload(Chunk chunk) throws IOException {
		RandomAccessFile file=new RandomAccessFile(filename,"rw");
		byte[] bytes = chunk.getBytes();
		if (size > bytes.length + chunk.getStart()) {
			seek(chunk.getStart());
			write(bytes, chunk.getStart(), bytes.length);
			dateModified = cal.getTime();
			return dateModified;
		}
		file.close();
		//TODO return date?? fix
		//TODO file out of memory
		return null;
		
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
