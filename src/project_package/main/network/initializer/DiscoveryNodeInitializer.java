package project_package.main.network.initializer;

import java.io.IOException;
import java.net.UnknownHostException;

import project_package.main.network.DiscoveryNode;
import project_package.main.network.config.Config;
import project_package.main.service.LoggerService;
import project_package.main.thread.ServerThread;

/**
 * Initializer koji pokreće client thread i zatvara ga
 *
 * @author Iva
 */
public class DiscoveryNodeInitializer {
    public static void initialize(String[] args) {

        DiscoveryNode d = null;

        try {
            d = new DiscoveryNode(Config.DIS_HOST, Config.DIS_PORT);

            //pokreni server
            ServerThread serverThread;
            serverThread = new ServerThread(d);

            LoggerService.info(d.getClass(), d.toString() + " listening");
            serverThread.start();
        } catch (UnknownHostException e) {
            LoggerService.error(DiscoveryNodeInitializer.class, e.getMessage());
        } catch (IOException e) {
            LoggerService.error(DiscoveryNodeInitializer.class, e.getMessage());
        }

    }
}