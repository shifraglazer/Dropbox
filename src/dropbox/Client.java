package dropbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Client implements ReaderListener {

	private Socket socket;
	private OutputStream out;
	private static final int CHUNK_SIZE = 512;
	private PrintWriter write;
	private List<ClientCommand> commands;
	private FileCache fileCache;

	public Client(String directory) throws UnknownHostException, IOException {
		fileCache = new FileCache(directory);
		commands = new ArrayList<ClientCommand>();
		// DownloadCommand download = new DownloadCommand(null, null, 0, 0);
		ClientChunk chunk = new ClientChunk();
		SyncCommand sync = new SyncCommand();
		FileCommand file = new FileCommand();
		FilesCommand files = new FilesCommand();
		commands.add(files);
		commands.add(chunk);
		commands.add(sync);
		commands.add(file);

		socket = new Socket("localhost", 6003);
		new ReaderThread(socket, this).start();
		out = socket.getOutputStream();
		write = new PrintWriter(out);
		requestFiles();
	}

	public void requestFiles() {
		writeMessage("LIST");
	}

	public void writeMessage(String message) {
		write.println(message);
		write.flush();
	}

	@Override
	public void onLineRead(Socket socket, String line) {
		String string = line;

		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).matches(line)) {
				try {
					commands.get(i).executeCommand(this);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void requestUpdate(String filename, long lastmodified, int size) {

		try {
			if (lastmodified > fileCache.findFile(filename).lastModified()) {
				requestDownloadFile(filename, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(filename, size);
		}
	}

	public void syncFile(String filename, long lastmodified, int size) {

		// TODO remove/fix file not found
		try {
			if (lastmodified > fileCache.findFile(filename).lastModified()) {
				requestDownloadFile(filename, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(filename, size);
		}

	}

	public void addChunk(Chunk chunk) throws IOException{
		fileCache.addChunk(chunk);
	}

	public void requestDownloadFile(String filename, int size) {
		int downloadedSize = 0;
		while (downloadedSize < size) {
			if(size-downloadedSize<CHUNK_SIZE){
				downloadChunkMsg(filename, downloadedSize, size-downloadedSize);
				downloadedSize=size;
			}
			else{
			downloadChunkMsg(filename, downloadedSize, CHUNK_SIZE);
			downloadedSize+=CHUNK_SIZE;
			}
		}
	}

	public void downloadChunkMsg(String filename, int downloadedSize,
			int chunkSize) {
		writeMessage("DOWNLOAD " + filename + " " + downloadedSize + " "
				+ chunkSize);
		

	}

	@Override
	public void onCloseSocket(Socket socket) {

	}
	public static void main(String [] args){
		try {
			Client client=new Client("client1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
