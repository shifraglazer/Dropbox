package dropbox;

import java.net.Socket;

public class ChunkCommand extends Command{

	private String filename;
	private int offset;
	private int size;
	public ChunkCommand(Socket socket, String filename, int offset, int size) {
		super(socket);
		this.filename = filename;
		this.offset = offset;
		this.size = size;
	}
	@Override
	void executeCommand(World world) {
		
		
	}

}
