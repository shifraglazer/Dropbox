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
}
