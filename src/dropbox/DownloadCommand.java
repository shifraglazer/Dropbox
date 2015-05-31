package dropbox;

import java.net.Socket;
import java.util.Base64;



public class DownloadCommand extends Command{

	private String filename;
	private int offset;
	private int chunksize;
	public DownloadCommand(Socket socket,String filename, int offset,
			int chunksize) {
		super(socket);
		this.filename = filename;
		this.offset = offset;
		this.chunksize = chunksize;
	}

	@Override
	public void executeCommand(World world) {
		ChunkCommand chunk = world.getChunkCommand(filename, offset, chunksize);
		world.writeCommand(chunk);
		writeMessage(socket, Base64.getEncoder().encodeToString(chunk.getBytes()));
	}

}
