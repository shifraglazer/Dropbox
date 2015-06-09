package dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Client implements ReaderListener {

	private static final int CHUNK_SIZE = 512;
	private Socket socket;
	private OutputStream out;
	private PrintWriter write;
	private List<ClientCommand> commands;
	private FileCache fileCache;
	private Map<String, Long> filesFromServer;
	private int numFilesInServer;
	private ArrayList<File> toBeAddedToServer;

	public Client(String directory) throws UnknownHostException, IOException {
		fileCache = new FileCache(directory);
		commands = new ArrayList<ClientCommand>();
		filesFromServer = new HashMap<String, Long>();
		toBeAddedToServer = new ArrayList<File>();

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

	public void filesCmd(int numFilesInServer){
		this.numFilesInServer = numFilesInServer;

		if(numFilesInServer == 0){
			uploadAll();
		}
	}

	public void uploadFile(String line) {
		StringTokenizer token = new StringTokenizer(line);
		String file = token.nextToken();
		file = token.nextToken();
		long lastModified = Long.valueOf(token.nextToken());
		int size = Integer.valueOf(token.nextToken());

		filesFromServer.put(file, lastModified);

		if(filesFromServer.size() == numFilesInServer){
			File[] files = fileCache.getFiles();

			for(File f : files){
				if (!filesFromServer.containsKey(f.getName())){
					toBeAddedToServer.add(f);
				}
				else if(filesFromServer.get(f.getName()) < f.lastModified()){
					toBeAddedToServer.add(f);
				}
			}
			uploadToServer();
		}

	}

	public void uploadAll(){
		File[] files = fileCache.getFiles();

		for(File f : files){
			toBeAddedToServer.add(f);
		}
		uploadToServer();
	}

	public void uploadToServer(){

		if(toBeAddedToServer != null){
			File file;
			int offset = 0;
			int length;
			for(int i = 0; i < toBeAddedToServer.size(); i++){
				file = toBeAddedToServer.get(i);
				while (offset < file.length()) {
					if (file.length() - offset < CHUNK_SIZE) {
						length = (int) (file.length() - offset);
					} 
					else {
						length = CHUNK_SIZE;
					}
					System.out.println("upload chunk to sever..");

					Chunk chunk;
					try {
						chunk = fileCache.getChunk(file.getName(), offset, length);
						writeMessage("CHUNK " + file.getName() + " " + file.lastModified() + " " + file.length() + " " + offset + " " + Base64.getEncoder().encodeToString(chunk.getBytes()));
						offset += length;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void requestFiles() {
		writeMessage("LIST");
	}

	public void writeMessage(String message) {
		System.out.println("client requesting:" + message);
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
		try {
			if (lastmodified > fileCache.findFile(filename).lastModified()) {
				requestDownloadFile(filename, size);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found..or is newer");
			requestDownloadFile(filename, size);
		}

	}

	public void addChunk(Chunk chunk, long lastModified) throws IOException {
		fileCache.upload(chunk, lastModified);
	}

	public void requestDownloadFile(String filename, int size) {
		writeMessage("DOWNLOAD " + filename);
	}

	@Override
	public void onCloseSocket(Socket socket) {

	}

	public static void main(String[] args) {
		try {
			Client client = new Client("client2");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
