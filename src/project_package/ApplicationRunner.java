package project_package;

import project_package.main.network.initializer.DiscoveryNodeInitializer;
import project_package.main.network.initializer.PeerNodeInitializer;
import project_package.main.network.storage.Storage;

/***
* klasa koja omogućava pokretanje programa u konzoli
 *
 * @author Iva Tutiš
 */
public class ApplicationRunner {

    public static void main(String[] args) {

        //inicijaliziramo discovery
        String[] args2 = {};
        DiscoveryNodeInitializer.initialize(args2);
        //PASSED

        //inicijaliziramo peera
        String[] args3 = {"8081", "1"};
        System.out.println(args3.length);
        PeerNodeInitializer.initialize(args3);
        //PASSED

        //inicijaliziramo spremište podataka
        String[] args1 = {"8082", "0"};
        Storage.initialize(args1);
        //PASSED
    }
}
