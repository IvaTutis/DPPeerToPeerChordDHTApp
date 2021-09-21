package project_package.main.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import project_package.main.tcp_info.config.TCPconfig;
import project_package.main.service.DataSenderService;
import project_package.main.service.IDService;
import project_package.main.network.config.Config;
import project_package.main.service.LoggerService;

/**
 * klasa Peer node-a
 *
 * @author Iva
 */
public class PeerNode extends Node {

    private static final long serialVersionUID = 1113799434508676095L;
    private int temp;
    private int id;
    private PeerNode pred;
    private PeerNode n1;
    private PeerNode[] ftNode;
    private PeerNode queryNode;
    private int[] ftStart;
    private int[] snapshot;
    private ArrayList<String> fileNames;
    private ArrayList<Integer> fileKeys;
    private boolean flag = false;

    //---------------------------------------CONSTRUCTORS

    public PeerNode(String host, int port) {
        super(host, port);
        pred = null;
        ftNode = new PeerNode[Config.FINGER_TABLE_SIZE];
        ftStart = new int[Config.FINGER_TABLE_SIZE];
        snapshot = new int[Config.FINGER_TABLE_SIZE];
        fileKeys = new ArrayList<>();
        fileNames = new ArrayList<>();
        id = IDService.generateID();
        for (int i = 0; i < ftStart.length; i++) {
            int value = (int) (id + Math.pow(2, i));
            ftStart[i] = IDService.convertToCircleID(value);
        }
    }

    public PeerNode(String host, int port, int ID) {
        super(host, port);
        pred = null;
        ftNode = new PeerNode[Config.FINGER_TABLE_SIZE];
        ftStart = new int[Config.FINGER_TABLE_SIZE];
        snapshot = new int[Config.FINGER_TABLE_SIZE];
        fileKeys = new ArrayList<>();
        fileNames = new ArrayList<>();
        this.id = ID;
        for (int i = 0; i < ftStart.length; i++) {
            int value = (int) (ID + Math.pow(2, i));
            ftStart[i] = IDService.convertToCircleID(value);
        }
    }

    public PeerNode findSucc(PeerNode n, int id) {
        n = query(findPred(n, id));
        return n.ftNode[0];
    }

    public PeerNode findPred(PeerNode n, int id) {
        PeerNode p = n;
        PeerNode checkNode = null;
        while (!isBetween(id, p.id, p.ftNode[0].id, false, true)) {

            if (checkNode != null) {
                if (checkNode.equals(p))
                    break;
            }

            checkNode = p;
            p = findClosest(p, id);

            if (p.equals(this))
                break;
        }
        return p;
    }

    public PeerNode findClosest(PeerNode n, int id) {
        for (int i = n.ftNode.length - 1; i >= 0; i--) {
            if (n.ftNode[i] == null) {
                continue;
            }
            if (isBetween(n.ftNode[i].id, n.id, id, false, false)) {
                return n.ftNode[i];
            }
        }
        return n;
    }


    public void exit(PeerNode p){
        PeerNode succ = p.ftNode[0];

        // redistribute files

        succ.setPred(this);
        update(succ, TCPconfig.UPDATE_PRED);

        for(int i = 0; i < Config.FINGER_TABLE_SIZE; i++){
            if(ftNode[i].id == p.id){
                ftNode[i] = succ;
            }
        }

        LoggerService.info(PeerNode.class, getNickname() + " updated");
        printNodeInfo();
    }

    public void join(PeerNode p) {

        if (!equals(p)) {
            p = query(p);
            initFingerTable(p);
            updateOthers(p);
        }

        // this is the only project_package.main.network in the network
        else {
            for (int i = 0; i < ftNode.length; i++) {
                ftNode[i] = this;
            }
            setPred(this);
        }

        LoggerService.info(PeerNode.class, getNickname() + " updated");
        printNodeInfo();

        redistribute();
        LoggerService.info(PeerNode.class, getNickname() + " redistributing files");
        printNodeInfo();

    }

    public void initFingerTable(PeerNode p) {
        ftNode[0] = query(findSucc(p, ftStart[0]));
        snapshot[0] = ftNode[0].id;

        PeerNode succ = ftNode[0];
        setPred(succ.pred);
        succ.setPred(this);
        update(succ, TCPconfig.UPDATE_PRED);

        for (int i = 0; i < ftNode.length - 1; i++) {
            if (isBetween(ftStart[i + 1], id, ftNode[i].id, true, false)) {
                ftNode[i + 1] = ftNode[i];
            } else {
                ftNode[i + 1] = query(findSucc(p, ftStart[i + 1]));
                if (isBetween(id, ftStart[i + 1], ftNode[i + 1].id, true, false) && id < ftNode[i + 1].id) {
                    ftNode[i + 1] = this;
                }
            }
            snapshot[i + 1] = ftNode[i + 1].id;
        }
        LoggerService.info(PeerNode.class, getNickname() + " initialized");
        printNodeInfo();
    }

    public void updateOthers(PeerNode t) {
        for (int i = 0; i < ftNode.length; i++) {
            if (id > ftStart[i] && id < snapshot[i]) {
                ftNode[i] = this;
            }
        }
        for (int i = 0; i < ftNode.length; i++) {
            int value = (int) (id - Math.pow(2, i));
            value = IDService.convertToCircleID(value);

            PeerNode p = query(findPred(t, value));

            PeerNode temp = query(p.ftNode[0]);
            if (temp.id == value) {
                updateFingerTable(temp, this, i);
            }

            if (isBetween(ftStart[i], p.id, id, false, true) && ftNode[i].id == p.id) {
                ftNode[i] = this;
            }
            if (!p.equals(this) && isBetween(p.ftStart[i], p.id, id, false, true)) {
                updateFingerTable(p, this, i);
            }
        }

    }

    public void updateFingerTable(PeerNode n1, PeerNode s, int i) {

        if (isBetween(s.id, n1.id, n1.ftNode[i].id, true, false)) {
            n1.ftNode[i] = s;
            update(TCPconfig.UPDATE_INDEX, n1, i);
            PeerNode p = query(n1.pred);
            if (!p.equals(s)) {
                if (isBetween(s.id, p.ftStart[i], p.ftNode[i].id, true, false)) {
                    updateFingerTable(p, s, i);
                }
            }
        }
    }

    public void update(PeerNode p, int updateType) {
        try {
            DataSenderService sender = new DataSenderService(p);
            sender.sendData(TCPconfig.UPDATE, p, updateType);
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }
    }

    public void update(int code, PeerNode p, int index) {
        try {
            DataSenderService sender = new DataSenderService(p);
            sender.sendData(code, this, index);
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }
    }


    public void getUpdate(PeerNode p, int updateType) {
        switch (updateType) {
            case (TCPconfig.UPDATE_PRED):
                setPred(p.pred);
                break;

            case (TCPconfig.UPDATE_ID):
                id = p.id;
                break;

        }

        LoggerService.info(PeerNode.class, "Updated -- " + updateType);
        printNodeInfo();
    }

    public void getUpdate(int index, PeerNode p) {


        ftNode[index] = p;

        LoggerService.info(PeerNode.class, "Updated -- index " + index);
        printNodeInfo();
    }



    public synchronized PeerNode query(PeerNode p) {
        queryNode = null;
        try {
            DataSenderService sender = new DataSenderService(p);
            sender.sendData(TCPconfig.QUERY_REQUEST, this);
            wait();
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        } catch (InterruptedException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }
        return queryNode;
    }

    public void sendQuery(PeerNode p) {
        try {
            DataSenderService sender = new DataSenderService(p);
            sender.sendData(TCPconfig.QUERY_RESPOND, this);
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }
    }

    public synchronized void getQuery(PeerNode p) {
        queryNode = p;
        notify();
    }

    public void writeFileS(PeerNode node, byte[] imageBytes, String fileName, int key) {
        try {
            File dir = new File("tmp");
            if (!dir.exists()) {
                dir.mkdir();
            }

            //unix vs windows
            //File file = new File(dir + "/" + fileName);
            File file = new File(fileName);
            Files.write(file.toPath(), imageBytes);

            fileKeys.add(key);
            fileNames.add(fileName);

            LoggerService.info(PeerNode.class, "File with key [" + key + "] is stored");
            printNodeInfo();

            // send respond back
            DataSenderService sender = new DataSenderService(node);
            sender.sendData(TCPconfig.FILE_TRANSFER_RESPOND, this);
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }

    }

    public synchronized void writeFileP(PeerNode node, byte[] imageBytes, String fileName, int key) {
        try {
            File dir = new File("tmp");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dir + "/" + fileName);
            Files.write(file.toPath(), imageBytes);

            fileKeys.add(key);
            fileNames.add(fileName);

            LoggerService.info(PeerNode.class, "File with key [" + key + "] is stored");
            printNodeInfo();

            // send respond back
            notify();
        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }

    }

    public synchronized void redistribute() {
        if (this != ftNode[0]) {
            PeerNode p = query(ftNode[0]);
            for (int i = 0; i < p.fileKeys.size(); i++) {
                int key = p.fileKeys.get(i);
                if (isBetween(id, key, p.id, true, false) && key != p.id) {
                    try {
                        DataSenderService sender = new DataSenderService(p);
                        sender.sendData(TCPconfig.REDIS_REQUEST, this, key);
                        wait();

                    } catch (IOException e) {
                        LoggerService.error(PeerNode.class, e.getMessage());
                    } catch (InterruptedException e) {
                        LoggerService.error(PeerNode.class, e.getMessage());
                    }
                }
            }
        }

    }

    public void sendFile(PeerNode p, int key) {
        try {
            int index = fileKeys.indexOf(key);
            if (index == -1) {
                LoggerService.error(PeerNode.class, "File not found");
                DataSenderService sender = new DataSenderService(p);
                sender.sendData(TCPconfig.QUERY_RESPOND, this);
            } else {
                String fileName = fileNames.get(index);
                File file = new File("tmp/" + fileName);

                DataSenderService sender = new DataSenderService(p);
                sender.sendData(TCPconfig.REDIS_RESPOND, this, file, fileName, key);

                if (file.delete()) {
                    LoggerService.info(PeerNode.class, "File " + fileName + " is deleted");
                } else {
                    LoggerService.error(PeerNode.class, "Delete operation failed");
                }

                fileKeys.remove(index);
                fileNames.remove(index);

                LoggerService.info(PeerNode.class, getNickname() + " Redistributing Files ...");
                printNodeInfo();
            }

        } catch (IOException e) {
            LoggerService.error(PeerNode.class, e.getMessage());
        }

    }

    public void printNodeInfo() {
        String str = "\n*****PEER NODE INFO*****\n";
        str += getNickname();
        str += "\nSucc: " + ftNode[0] + " | Pred: " + pred;
        str += "\nFinger Node: " + Arrays.toString(ftNode);
        str += "\nFinger Start: " + Arrays.toString(ftStart);
        str += "\nFile Keys: " + Arrays.toString(fileKeys.toArray());
        str += "\n";
        System.out.println(str);
    }

    public boolean equals(PeerNode p) {
        return getHost().equalsIgnoreCase(p.getHost()) && getPort() == p.getPort();
    }

    public String getNickname() {
        return "[" + id + "-" + getHost() + "-" + getPort() + "]";
    }

    public String toString() {
        return String.valueOf(id);
    }

    public void setPred(PeerNode p) {
        pred = p;
    }

    public void regeID() {
        id = IDService.generateID();
    }

    public void setID(int newID) {
        id = newID;
    }

    public PeerNode[] getFT() {
        return ftNode;
    }

    public PeerNode getPred(){
        return pred;
    }

    public int getID() {
        return id;
    }

    public boolean isBetween(int id, int min, int max, boolean left, boolean right) {
        if (left == true && right == true) {
            if (min < max) {
                return (id >= min && id <= max);
            } else {
                return (id >= min || id <= max);
            }
        } else if (left == true && right == false) {
            if (min < max) {
                return (id >= min && id < max);
            } else {
                return (id >= min || id < max);
            }
        } else if (left == false && right == true) {
            if (min < max) {
                return (id > min && id <= max);
            } else {
                return (id > min || id <= max);
            }
        } else {
            if (min < max) {
                return (id > min && id < max);
            } else {
                return (id > min || id < max);
            }
        }
    }

}