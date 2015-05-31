package dropbox;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


public class WriterThread extends Thread {

	private Map<String, Socket> queue;
	private FileCache fileCache;
	ArrayList<Socket> sockets;


	public WriterThread(Map<String, Socket> queue, FileCache fileCache, ArrayList<Socket> sockets) {
		this.queue = queue;
		this.fileCache = fileCache;
		this.sockets = sockets;
	}

	@Override
	public void run() {

		while (true) {

			synchronized (queue) {
				Iterator<String> iter = queue.keySet().iterator();
				while (iter.hasNext()) {
					String string;
					string = iter.next();
					Socket socket = queue.get(string);
					StringTokenizer token = new StringTokenizer(string);
					String cmd = token.nextToken();
					switch (cmd) {
					case "LIST":{
						File[] list = fileCache.getFiles();

						try {
							writeMessage(socket, "FILES "+String.valueOf(list.length));

							for(File file:list){
								writeMessage(socket, "FILE "+file.getName()+" "+file.lastModified()+" "+file.length());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					case "CHUNK": {
						String filename=token.nextToken();
						int lastmodified=Integer.valueOf(token.nextToken());
						int size=Integer.valueOf(token.nextToken());
						int offset=Integer.valueOf(token.nextToken());
						byte[] chunk = Base64.getDecoder().decode(token.nextToken());
						Chunk aChunk=new Chunk(filename,chunk,offset);
						try {
							Date date=fileCache.addChunk(aChunk);
							if(offset+chunk.length==size){
								sync(filename,(int)(long)date.getTime(),size);
							}
						} catch (IOException | FileOutOfMemoryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
					case "DOWNLOAD": {
						String filename =token.nextToken();
						int start=Integer.valueOf(token.nextToken());
						int size=Integer.valueOf(token.nextToken());
						Chunk chunk;

						try {
							chunk = fileCache.getChunk(filename, start, size);

							writeMessage(socket, Base64.getEncoder().encodeToString(chunk.getBytes()));

						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					
					case "LOGIN": {
						break;
					}
					}

				}
			}
		}
	}


	public void sync(String filename,int lastmodified,int filesize) throws IOException {
		synchronized(sockets){
		for(Socket asocket : sockets){
			writeMessage(asocket, "SYNC "+filename+" "+lastmodified+" "+filesize);
		}
		}
	}

	public void writeMessage(Socket s, String msg) throws IOException {
		OutputStream stream = s.getOutputStream();
		PrintWriter write = new PrintWriter(stream);
		write.println(msg);
		write.flush();
	}


}
