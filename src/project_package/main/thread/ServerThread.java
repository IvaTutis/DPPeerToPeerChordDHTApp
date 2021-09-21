package project_package.main.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import project_package.main.network.Node;
import project_package.main.tcp_info.ConnectionInfo;

/**
 * Klasa koja spaja network na socket i port.
 * Ujedno server thread
 *
 * @author Iva
 */
public class ServerThread extends Thread {

	private Node node;
	private ServerSocket serverSocket;
	private int port;

	//konstruktor za server thread
	public ServerThread(Node node) throws IOException {
		this.node = node;
		port = node.getPort();
		serverSocket = new ServerSocket(port);
	}


	//run thread
	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				//spoji se na socket
				socket = serverSocket.accept();
				ConnectionInfo conn = new ConnectionInfo(node, socket);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				continue;
			}

		}
	}

}
