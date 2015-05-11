package dropbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements ReaderListener{

	private Socket socket;
	private Map<String,Socket> queue;
	private ArrayList<Socket> sockets;
	private WriterThread write;
	public Server() {
		queue= Collections.synchronizedMap(new LinkedHashMap<String,Socket>());
		sockets= new ArrayList<Socket>();
		write=new WriterThread(queue);
		write.start();
		try {
			ServerSocket serverSocket = new ServerSocket(6003); // port num sent
			while( true){
			socket = serverSocket.accept();
			synchronized(sockets){
			sockets.add(socket);
			}
			ReaderThread thread=new ReaderThread(socket,this);
			thread.start();
			}

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	

	@Override
	public void onLineRead(Socket socket,String line) {
		queue.put(line, socket);
	}


	@Override
	public void onCloseSocket(Socket socket) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String args[]){
		Server server=new Server();
	}



}