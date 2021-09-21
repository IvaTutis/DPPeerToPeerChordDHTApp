package project_package.main.service;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.file.Files;
import project_package.main.network.Node;

/**
 * Klasa koja formulira sent podatke
 *
 *
 * @author Iva
 */
public class DataSenderService implements Serializable {

	private ObjectOutputStream stream;
	private Socket socket;

	// constructors
	public DataSenderService(Socket socket) throws IOException{
		stream = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}
	
	public DataSenderService(Node node) throws IOException {
		Socket socket = new Socket(node.getHost(), node.getPort());
		stream = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}
	
	public DataSenderService(String host, int port) throws IOException {
		Socket socket = new Socket(host, port);
		stream = new ObjectOutputStream(socket.getOutputStream());
		this.socket = socket;
	}

	//funkcije za slanje podataka

	public void sendData(int code, Node node) throws IOException {
		stream.writeInt(code);
		stream.writeObject(node);
		stream.flush();
		socket.shutdownOutput();
	}
	
	public void sendData(int code, Node node, int updateType) throws IOException {
		stream.writeInt(code);
		stream.writeObject(node);
		stream.writeInt(updateType);
		stream.flush();
		socket.shutdownOutput();
	}
	
	public void sendData(int code, Node node, int updateType, int index) throws IOException {
		stream.writeInt(code);
		stream.writeObject(node);
		stream.writeInt(updateType);
		stream.writeInt(index);
		stream.flush();
		socket.shutdownOutput();
	}
	
	
	public void sendData(int code, Node n, File file, String fileName, int key) throws IOException{
		stream.writeInt(code);
		stream.writeObject(n);
		byte[] content = Files.readAllBytes(file.toPath());
		stream.writeObject(content);
		stream.writeObject(fileName);
		stream.writeInt(key);
		stream.flush();
		socket.shutdownOutput();
	}
}
