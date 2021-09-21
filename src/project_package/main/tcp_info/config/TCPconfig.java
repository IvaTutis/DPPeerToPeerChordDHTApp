package project_package.main.tcp_info.config;

/**
 * klasa koja ima config podatke za tcp
 *
 * @author Iva
 */
public class TCPconfig {

	//join network
	public static final int PEER_TO_DISCOVERY_JOIN = 1;

	//discovery network to peer network
	public static final int DISCOVERY_TO_PEER = 3;

	//respond i request
	public static final int QUERY_REQUEST = 4;
	public static final int QUERY_RESPOND = 5;

	// get random network iz Storage-a
	public static final int STORE_TO_DISCOVERY = 6;
	public static final int DISCOVERY_TO_STORE = 7;
	
	// prebaci file
	public static final int FILE_TRANSFER_REQUEST = 8;
	public static final int FILE_TRANSFER_RESPOND = 9;
	
	// update
	public static final int UPDATE = 10;
	public static final int UPDATE_PRED = 11;
	public static final int UPDATE_ALL = 12;
	public static final int UPDATE_ID = 13;
	public static final int UPDATE_INDEX = 17;
	
	// redistribiraj
	public static final int REDIS_REQUEST = 14;
	public static final int REDIS_RESPOND = 15;
	
	// exit
	public static final int PEER_TO_DISCOVERY_EXIT = 2;
	public static final int PEER_TO_PEER_EXIT = 16;
}
