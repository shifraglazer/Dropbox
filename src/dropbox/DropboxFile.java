package dropbox;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

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

}
