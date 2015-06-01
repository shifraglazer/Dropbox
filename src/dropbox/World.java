package dropbox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;

public class World {

	protected FileCache fileCache;
	protected Socket socket;

	public World() {
		fileCache = new FileCache();
	}

	public Chunk getChunk(String filename, int offset, int size) throws MalformedURLException, IOException {
		return fileCache.getChunk(filename, offset, size);
	}

	public void writeCommand(Command command) {
		writeCommand(command);
	}

	public ChunkCommand getChunkCommand(String filename, int offset, int chunksize) {
		return fileCache.getChunkCommand(filename, offset, chunksize);
	}
}
