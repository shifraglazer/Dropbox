package dropbox;

public class FileOutOfMemoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FileOutOfMemoryException(){
		super("No room left in current directory");
	}

}
