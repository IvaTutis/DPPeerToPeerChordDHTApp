package project_package.main.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import project_package.main.service.DataSenderService;
import project_package.main.service.IDService;
import project_package.main.service.LoggerService;
import project_package.main.tcp_info.config.TCPconfig;

/**
 * klasa discovery cvora
 *
 * @author Iva
 */
public class DiscoveryNode extends Node {

    private ArrayList<PeerNode> peerNodeList;

    //konstruktor
    public DiscoveryNode(String host, int port) {
        super(host, port);
        peerNodeList = new ArrayList<PeerNode>();
    }

    public void join(PeerNode p) {
        //provjeravam ID collision
        while (checkCollision(p)) {
            int oldID = p.getID();
            p.regeID();
            p.update(p, TCPconfig.UPDATE_ID);
            LoggerService.info(DiscoveryNode.class, "Collision in ID " + oldID + ", new ID is " + p.getID());
        }

        // podslji random project_package.main.network nazad
        try {
            LoggerService.info(DiscoveryNode.class, p.getNickname() + " joined the system");

            DataSenderService sender = new DataSenderService(p);

            if (peerNodeList.size() == 0) {
                LoggerService.info(DiscoveryNode.class, "Send original project_package.main.network " + p + " back");
                sender.sendData(TCPconfig.DISCOVERY_TO_PEER, p);
            } else {
                PeerNode rand = getRandPeerNode();
                LoggerService.info(DiscoveryNode.class, "Send random project_package.main.network " + rand + " back");
                sender.sendData(TCPconfig.DISCOVERY_TO_PEER, rand);
            }

            peerNodeList.add(p);

        } catch (UnknownHostException e) {
            LoggerService.error(DiscoveryNode.class, e.getMessage());
        } catch (IOException e) {
            LoggerService.error(DiscoveryNode.class, e.getMessage());
        }

    }

    //izadji iz mreže
    public void exit(PeerNode p) {
        try {
            DataSenderService sender = new DataSenderService(p.getPred());
            sender.sendData(TCPconfig.PEER_TO_PEER_EXIT,	 p);
            LoggerService.info(DiscoveryNode.class, p.getNickname() + " exited the system");
            peerNodeList.remove(p);
        } catch (IOException e) {
            LoggerService.error(DiscoveryNode.class, e.getMessage());
        }
    }

    //provjeri za collision
    public boolean checkCollision(PeerNode pnode) {
        boolean check = false;
        for (int i = 0; i < peerNodeList.size(); i++) {
            if (peerNodeList.get(i).getID() == pnode.getID()) {
                check = true;
                break;
            }
        }
        return check;
    }

    //get random peer cvor i vrati ga
    public PeerNode getRandPeerNode() {
        int index = IDService.generateRandInt(0, peerNodeList.size());
        return peerNodeList.get(index);
    }

    //posalji rendom podatke iz peer cvora p
    public void getRandPeerNode(PeerNode p) {
        try {
            int index = IDService.generateRandInt(0, peerNodeList.size());
            PeerNode rand = peerNodeList.get(index);

            DataSenderService sender = new DataSenderService(p);
            sender.sendData(TCPconfig.DISCOVERY_TO_STORE, rand);

        } catch (IOException e) {
            LoggerService.error(DiscoveryNode.class, e.getMessage());
        }
    }

}