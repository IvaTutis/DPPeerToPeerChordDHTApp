package project_package.main.network;

import java.io.Serializable;

 /**
  * generic klasa za cvor
  *
  * @author Iva
  */
public class Node implements Serializable {

    private String host;
    private int port;

    //--konstruktori

    public Node(){

    }

    public Node(String host, int port){
        this.host = host;
        this.port = port;
    }

    //---getteri

     public String getNickname(){
         return host + "-" + String.valueOf(port);
     }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    //za provjeravanje je li rijec o istom cvoru
    public boolean equals(Node node){
        return host.equalsIgnoreCase(node.getHost()) && port == node.getPort();
    }

    //toString
     public String toString(){
         return "[" + host + ":" + port + "]";
     }



 }
