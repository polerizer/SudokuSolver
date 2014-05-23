import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Jan 10, 2013
 * FileHandler.java
 * Daniel Pok
 * AP Java 6th
 */


public class FileHandler {
	private BufferedReader fin;
	private boolean ready = false;

	public FileHandler() {
		ready = false;
		fin = null;
	}

	public FileHandler(String fileName) {
		try {
			fin = new BufferedReader(new FileReader(fileName));
			ready = true;
		} catch (FileNotFoundException e) {
			System.err.println("Failed to open the file \"" + fileName + "\" for reading.");
			ready = false;
			quietClose(fin);
			fin = null;
		}
	}

	public String readLine(){
		try {
			if(ready && fin.ready()){
				return fin.readLine();
			} else {
				ready = false;
				return "";
			}
		} catch (IOException e) {
			ready = false;
			return "";
		}
	}
	
	public void close() {
		quietClose(fin);
		fin = null;
		ready = false;
	}
	
	public void reset() {
		quietClose(fin);
		fin = null;
		ready = false;
	}
	
	public boolean ready() {
		return ready;
	}
	
	public boolean open(String fileName) {
		try {
			fin = new BufferedReader(new FileReader(fileName));
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Failed to open the file for reading.");
			ready = false;
			fin = null;
			return false;
		}
	}

	private static void quietClose(BufferedReader reader) {
		try {
			if(reader != null){
				reader.close();
			}
		} catch (IOException e) {
			reader = null;
		}
	}

}
