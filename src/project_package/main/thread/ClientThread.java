package project_package.main.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import project_package.main.network.storage.Storage;
import project_package.main.network.Node;
import project_package.main.network.PeerNode;
import project_package.main.tcp_info.config.TCPconfig;
import project_package.main.service.LoggerService;
import project_package.main.network.DiscoveryNode;

/**
 * Klasa koja spaja network na socket i port.
 * Ujedno client thread
 *
 * @author Iva
 */
public class ClientThread extends Thread {

	private Socket socket;
	private ObjectInputStream ois;
	private Node node;

	//konstruktor za client thread
	public ClientThread(Node node, Socket socket) throws IOException {
		this.node = node;
		this.socket = socket;
		ois = new ObjectInputStream(this.socket.getInputStream());
	}

	//run thread
	@Override
	public void run() {
		while (socket != null) {
			try {

				int code = ois.readInt();
				PeerNode p = (PeerNode) ois.readObject();

				switch (code) {

				// REQUESTE HANDLAJ CASE BY CASE

				case (TCPconfig.PEER_TO_DISCOVERY_JOIN):
					((DiscoveryNode) node).join(p);
					break;

				case (TCPconfig.PEER_TO_DISCOVERY_EXIT):
					((DiscoveryNode) node).exit(p);
					break;

				case (TCPconfig.DISCOVERY_TO_PEER):
					((PeerNode) node).join(p);
					break;

				case (TCPconfig.PEER_TO_PEER_EXIT):
					((PeerNode) node).exit(p);
					break;

				case (TCPconfig.QUERY_REQUEST):
					((PeerNode) node).sendQuery(p);
					break;

				case (TCPconfig.QUERY_RESPOND):
					((PeerNode) node).getQuery(p);
					break;

				case (TCPconfig.UPDATE):
					int updateType = ois.readInt();
					((PeerNode) node).getUpdate(p, updateType);
					break;
					
				case (TCPconfig.UPDATE_INDEX):
					int index = ois.readInt();
					((PeerNode) node).getUpdate(index, p);
					break;

				// store to discovery operation
				case (TCPconfig.STORE_TO_DISCOVERY):
					((DiscoveryNode) node).getRandPeerNode(p);
					break;

				case (TCPconfig.DISCOVERY_TO_STORE):
				case (TCPconfig.FILE_TRANSFER_RESPOND):
					((Storage) node).sync(p);
					break;

				// transfer files
				case (TCPconfig.FILE_TRANSFER_REQUEST):
					byte[] fileBytes = (byte[]) ois.readObject();
					String fileName = (String) ois.readObject();
					int key = ois.readInt();
					((PeerNode) node).writeFileS(p, fileBytes, fileName, key);
					break;

				// redistribute files
				case (TCPconfig.REDIS_REQUEST):
					key = ois.readInt();
					((PeerNode) node).sendFile(p, key);
					break;

				case (TCPconfig.REDIS_RESPOND):
					fileBytes = (byte[]) ois.readObject();
					fileName = (String) ois.readObject();
					key = ois.readInt();
					((PeerNode) node).writeFileP(p, fileBytes, fileName, key);
					break;

				default:
					LoggerService.error(ClientThread.class, "I don't understand your code");

				}

			} catch (ClassNotFoundException e) {
				LoggerService.error(ClientThread.class, e.getMessage());
			} catch (IOException e) {
				LoggerService.error(ClientThread.class, e.getMessage());
				break;
			}
		}
	}

}
