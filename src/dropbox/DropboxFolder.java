package dropbox;

import java.io.File;

public class DropboxFolder extends File{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	public DropboxFolder(String username){
		super(username);
		this.username=username;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
