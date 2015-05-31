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
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.digest.DigestUtils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

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
					Socket s = queue.get(string);
					StringTokenizer token = new StringTokenizer(string);
					String cmd = token.nextToken();
					switch (cmd) {
					case "LIST":{
						File[] list = fileCache.getFiles();

						try {
							writeMessage(s, new Integer(list.length).toString());

							for(File f:list){
								writeMessage(s, "FILE "+f.getName()+" "+f.lastModified()+" "+f.length());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					case "CHUNK": {
						String chunkEncoded = "";

						try {

							byte[] c = Base64.decode(chunkEncoded);
							Chunk obj = null;
							ByteArrayInputStream bis = null;
							ObjectInputStream ois = null;
							try {
								bis = new ByteArrayInputStream(c);
								ois = new ObjectInputStream(bis);
								obj = (Chunk) ois.readObject();

							} finally {
								if (bis != null) {
									bis.close();
								}
								if (ois != null) {
									ois.close();
								}
							}

						} catch (Base64DecodingException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}


						break;
					}
					case "DOWNLOAD": {
						String filename = "";
						int start = 0;
						int length = 0;

						Chunk c;

						try {
							c = fileCache.getChunk(filename, start, length);

							writeMessage(s, Base64.encode(c.getBytes()));

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					case "SYNC": {
						String filename = "";
						GregorianCalendar lastmodified = new GregorianCalendar();
						int filesize = 0;

						try {
							for(Socket socket : sockets){
								writeMessage(socket, "SYNC "+filename+" "+lastmodified.toString()+" "+filesize);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
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


	public void writeMessage(Socket s, String msg) throws IOException {
		OutputStream stream = s.getOutputStream();
		PrintWriter write = new PrintWriter(stream);
		write.println(msg);
		write.flush();
	}


}
