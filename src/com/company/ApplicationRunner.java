package com.company;

import main.Discovery;
import main.Peer;
import main.StoreData;

public class ApplicationRunner {

    public static void main(String[] args) {
        String[] args1 = {"8082", "0"};
        StoreData.main(args1);

        String[] args2 = {};
        Discovery.main(args2);

        String[] args3 = {"8081", "-1"};
        System.out.println(args3.length);
        Peer.main(args3);
    }
}
