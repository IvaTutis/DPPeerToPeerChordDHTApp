package project_package.test;

import project_package.main.network.PeerNode;

public class Test {
	public static void main(String[] args){
		
		PeerNode n = new PeerNode("iva", 8080);
		System.out.println(n.isBetween(2, 1, 1, false, false));
		
	}
}
