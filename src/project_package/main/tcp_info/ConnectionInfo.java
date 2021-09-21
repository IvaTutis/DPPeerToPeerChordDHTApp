package project_package.main.tcp_info;

import java.io.IOException;
import java.net.Socket;
import project_package.main.network.Node;
import project_package.main.service.DataSenderService;
import project_package.main.thread.ClientThread;

/**
 * Klasa koja posprema informacije o TCP vezi
 *
 *
 * @author Iva
 */
public class ConnectionInfo {
	private ClientThread receiver;
	private DataSenderService sender;
	private Socket socket;

	//konstruktor
	public ConnectionInfo(Node node, Socket socket) throws IOException {
		this.receiver = new ClientThread(node, socket);
		this.sender = new DataSenderService(socket);
		this.socket = socket;
		receiver.start();
	}
}
