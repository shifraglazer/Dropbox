package dropbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileCache {

	public List<File> files;
	public static final String ROOT="dropbox/";
	public FileCache(){
		new File(ROOT).mkdir();
		files=new ArrayList<File>();
	}

	public List<File> getFiles(String username){
		
	}
	public void addChunk(Chunk chunk){
		
	}
	public Chunk getChunk(String username,String filename,int start,int length){
		
	}
}
