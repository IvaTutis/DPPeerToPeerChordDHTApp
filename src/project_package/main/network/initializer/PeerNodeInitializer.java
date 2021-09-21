package project_package.main.network.initializer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import project_package.main.network.config.Config;
import project_package.main.service.IDService;
import project_package.main.service.LoggerService;
import project_package.main.network.PeerNode;
import project_package.main.service.DataSenderService;
import project_package.main.tcp_info.config.TCPconfig;
import project_package.main.thread.ServerThread;

/**
 * Initializer koji pokreće server thread i zatvara ga
 *
 * @author Iva
 */
public class PeerNodeInitializer {

    public static void initialize(String[] args) {
        if (args.length != 2) {
            LoggerService.error(PeerNodeInitializer.class, "project_package.main.network.initializer.PeerNodeInitializer [PORT] [ID (-1 for random)]");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);
        int ID = Integer.parseInt(args[1]);
        if (ID >= Math.pow(2, Config.ID_BIT_RANGE) || ID < 0) {
            LoggerService.error(PeerNodeInitializer.class, "Wrong ID space");
            System.exit(-1);
            }

        PeerNode p = null;
        try {

            //Stvori peerNode koji se spaja s navedenim podacima
            String host = IDService.getHostName();
            if (ID == -1) {
                p = new PeerNode(host, port);
            } else {
                p = new PeerNode(host, port, ID);
            }

            //Ukljući server thread
            ServerThread serverThread = new ServerThread(p);
            serverThread.start();

            //Ispiši u konzolu da slušaš
            LoggerService.info(p.getClass(), p.getNickname() + "Peer node is listening");

            // obavijesti discovery network da se pridruži mreži
            DataSenderService sender = new DataSenderService(Config.DIS_HOST, Config.DIS_PORT);
            sender.sendData(TCPconfig.PEER_TO_DISCOVERY_JOIN, p);


            // izađi iz mreže
            //exitNetwork(sender, p);

        } catch (UnknownHostException e) {
            System.out.println();
            System.out.println("Recheckiraj hostname ako si mijenjala s Unix-a na Windows ili na drugi kompjuter.");
            System.out.println("Promijeni hostname u src/project_package/main/network/config/Config.java.");
            LoggerService.error(p.getClass(), e.getMessage());
        } catch (IOException e) {
            LoggerService.error(p.getClass(), e.getMessage());
        }
    }


    public static void exitNetwork(DataSenderService sender, PeerNode p) throws IOException {

            Scanner scan = new Scanner(System.in);
            String input = scan.next();
            while (!input.equals("q!")) {
                switch (input) {
                    case ("exit"):
                        sender = new DataSenderService(Config.DIS_HOST, Config.DIS_PORT);
                        sender.sendData(TCPconfig.PEER_TO_DISCOVERY_EXIT, p);
                        LoggerService.info(p.getClass(), p.getNickname() + " exit");
                        break;
                }
                input = scan.next();
            }
            System.exit(0);

    }
}
