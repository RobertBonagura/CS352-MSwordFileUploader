package program4;


import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	
	Socket sock;
	
	private void run() {
		
		int portNumber = 5520;
		System.out.println("Server running.");
		try {
			ServerSocket servSock = new ServerSocket(portNumber);
			System.out.println("Waiting for a connection...");
			while (true) {
				sock = servSock.accept();
				Date timestamp = new java.util.Date();
				System.out.println("Got a connection." + timestamp.toString());
				InetAddress remoteAddress = sock.getInetAddress();
				int remotePort = sock.getPort();
				String filename = getNullTerminatedString();
				String filesize = getNullTerminatedString();
				long size = Long.parseLong(filesize);
				getFile(filename, size);
				System.out.println("I got filename and filesize");
				DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
				String ACK = "@";
				byte[] ack = ACK.getBytes();
				dos.write(ack);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		Server server = new Server();
		server.run();
	}
	
	/**
	* This method reads the bytes (terminated by ‘\0’) sent from the Client, 
	* one byte at a time and turns the bytes into a String.
	* Set up a loop to repeatedly read bytes until a ‘\0’ is reached.
	*/
	private String getNullTerminatedString(){
		
		try {
			BufferedInputStream input = (BufferedInputStream) sock.getInputStream();
			byte[] buffer = new byte[1024];
			int byteCount = 0;
			String currentByte;
			StringBuilder sb = new StringBuilder();
			while (true) {
				input.read(buffer);
				currentByte = ((Byte)buffer[byteCount]).toString();
				if (currentByte.contains("\0")) {
					break;
				}
				sb.append(currentByte);
				byteCount++;
			}
			String result = sb.toString();
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return "\0";
		
	}
	
	/**
	* This method takes an output file name and its file size, reads the binary * data (in a 1024-byte chunk) sent from the Client, and writes to the output * file a chunk at a time.
	* Use the FileOutputStream class to write bytes to a binary file
	* Set up a loop to repeatedly read and write chunks.
	*/
	private void getFile(String filename, long size){}

}
