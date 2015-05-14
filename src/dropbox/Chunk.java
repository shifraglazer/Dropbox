package dropbox;

public class Chunk {

	byte bytes[];
	int start ;
	String filename;
	
	public Chunk(String filename, byte[] bytes, int start){
		this.bytes=bytes;
		this.start=start;
		this.filename=filename;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}
