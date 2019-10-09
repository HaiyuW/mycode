import java.io.*;
import java.net.*;
import java.util.*;
/**
 * DhtClient
 * usage: DhtClient clientIP serverCfgFile [operation] [key] [value]
 * 
 * Parse server socket address from server's config file
 * Send get/put requests to server
 * Wait for reply packet
**/

public class DhtClient {
    private static boolean debug = true;

    public static void main(String args[]) throws Exception {
        // get server address and port
        InetAddress clientAdr = InetAddress.getByName(args[0]);
        String cfgFile = args[1];
        BufferedReader cfg=
                    new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(cfgFile),
                        "US-ASCII"));
        String s = cfg.readLine();
        String[] chunks = s.split(" ");
        cfg.close();
        InetAddress serverAdr = InetAddress.getByName(chunks[0]);
        int port = Integer.parseInt(chunks[1]);
        InetSocketAddress saddr = new InetSocketAddress(serverAdr,port);
        
        // create socket
        DatagramSocket sock = new DatagramSocket(0,clientAdr);
        
        /**
        * if operation is get, get the key
        * if operation is put, get the key and value.
        */
        Packet outpkt = new Packet();
        outpkt.type = args[2];
        if (outpkt.type.equals("get") && args.length > 3) outpkt.key = args[3];
        else if (outpkt.type.equals("put")) {
            if(args.length > 3) outpkt.key = args[3];
            if(args.length > 4) outpkt.val = args[4]; 
        }
        outpkt.tag = Math.abs(new Random().nextInt());
        outpkt.send(sock,saddr,debug);
        outpkt.clear();
        outpkt.receive(sock,debug);
        sock.close();
    }
}
