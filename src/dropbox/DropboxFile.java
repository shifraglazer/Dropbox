package dropbox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;

public class DropboxFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String filename;
	private int size;
	private String ext;

	private Date dateModified;

	public DropboxFile(String username, String filename, int size) {
		super(filename);
		this.username = username;
		this.filename = filename;
		this.size = size;
		GregorianCalendar cal = new GregorianCalendar();
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

	public void upload(Chunk chunk) {

	}

	// TODO deal with if not enough bytes file is shorter than request
	public Chunk getChunk(int start, int length) throws MalformedURLException,
			IOException {
		byte[] bytes = IOUtils.toByteArray(toURI().toURL());
		byte[] chunk = new byte[length];
		if(bytes.length-start>=length){
	
		for (int i = 0; i < chunk.length; i++) {
			chunk[i] = bytes[start + i];
		}
		}
		else{
			int left=bytes.length-start;
			for (int i = 0; i < left; i++) {
				chunk[i] = bytes[start + i];
			}
		}
		Chunk aChunk = new Chunk(filename, chunk, start);
		return aChunk;
		
	}
}
