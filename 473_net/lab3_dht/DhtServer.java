/** Server for simple distributed hash table that stores (key,value) strings.
 *  
 *  usage: DhtServer myIp numRoutes cfgFile [ cache ] [ debug ] [ predFile ]
 *  
 *  myIp	is the IP address to use for this server's socket
 *  numRoutes	is the max number of nodes allowed in the DHT's routing table;
 *  		typically lg(numNodes)
 *  cfgFile	is the name of a file in which the server writes the IP
 *		address and port number of its socket
 *  cache	is an optional argument; if present it is the literal string
 *		"cache"; when cache is present, the caching feature of the
 *		server is enabled; otherwise it is not
 *  debug	is an optional argument; if present it is the literal string
 *		"debug"; when debug is present, a copy of every packet received
 *		and sent is printed on stdout
 *  predFile	is an optional argument specifying the configuration file of
 *		this node's predecessor in the DHT; this file is used to obtain
 *		the IP address and port number of the predecessor's socket,
 *		allowing this node to join the DHT by contacting predecessor
 *  
 *  The DHT uses UDP packets containing ASCII text. Here's an example of the
 *  UDP payload for a get request from a client.
 *  
 *  CSE473 DHTPv0.1
 *  type:get
 *  key:dungeons
 *  tag:12345
 *  ttl:100
 *  
 *  The first line is just an identifying string that is required in every
 *  DHT packet. The remaining lines all start with a keyword and :, usually
 *  followed by some additional text. Here, the type field specifies that
 *  this is a get request; the key field specifies the key to be looked up;
 *  the tag is a client-specified tag that is returned in the response; and
 *  can be used by the client to match responses with requests; the ttl is
 *  decremented by every DhtServer and if <0, causes the packet to be discarded.
 *  
 *  Possible responses to the above request include:
 *  
 *  CSE473 DHTPv0.1
 *  type:success
 *  key:dungeons
 *  value:dragons
 *  tag:12345
 *  ttl:95
 *  
 *  or
 *  
 *  CSE473 DHTPv0.1
 *  type:no match
 *  key:dungeons
 *  tag:12345
 *  ttl:95
 *  
 *  Put requests are formatted similarly, but in this case the client typically
 *  specifies a value field (omitting the value field causes the pair with the
 *  specified key to be removed).
 *  
 *  The packet type “failure” is used to indicate an error of some sort; in 
 *  this case, the “reason” field provides an explanation of the failure. 
 *  The “join” type is used by a server to join an existing DHT. In the same
 *  way, the “leave” type is used by the leaving server to circle around the 
 *  DHT asking other servers to delete it from their routing tables.  The 
 *  “transfer” type is used to transfer (key,value) pairs to a newly added 
 *  server. The “update” type is used to update the predecessor, successor, 
 *  or hash range of another DHT server, usually when a join or leave even 
 *  happens. 
 *
 *  Other fields and their use are described briefly below
 *  clientAdr 	is used to specify the IP address and port number of the 
 *              client that sent a particular request; it is added to a request
 *              packet by the first server to receive the request, before 
 *              forwarding the packet to another node in the DHT; an example of
 *              the format is clientAdr:123.45.67.89:51349.
 *  relayAdr  	is used to specify the IP address and port number of the first
 *              server to receive a request packet from the client; it is added
 *              to the packet by the first server before forwarding the packet.
 *  hashRange 	is a pair of integers separated by a colon, specifying a range
 *              of hash indices; it is included in the response to a “join” 
 *              packet, to inform the new DHT server of the set of hash values
 *              it is responsible for; it is also included in the update packet
 *              to update the hash range a server is responsible for.
 *  succInfo  	is the IP address and port number of a server, followed by its
 *              first hash index; this information is included in the response
 *              to a join packet to inform the new DHT server about its 
 *              immediate successor; it’s also included in the update packet 
 *              to change the immediate successor of a DHT server; an example 
 *              of the format is succInfo:123.45.6.7:5678:987654321.
 *  predInfo	is also the IP address and port number of a server, followed
 *              by its first hash index; this information is included in a join
 *              packet to inform the successor DHT server of its new 
 *              predecessor; it is also included in update packets to update 
 *              the new predecessor of a server.
 *  senderInfo	is the IP address and port number of a DHT server, followed by
 *              its first hash index; this information is sent by a DHT to 
 *              provide routing information that can be used by other servers.
 *              It also used in leave packet to let other servers know the IP
 *              address and port number information of the leaving server.
 */

import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.*;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class DhtServer {
    private static int numRoutes;	// number of routes in routing table
    private static boolean cacheOn;	// enables caching when true
    private static boolean debug;	// enables debug messages when true

    private static HashMap<String,String> map;	// key/value pairs
    private static HashMap<String,String> cache;	// cached pairs
    private static List<Pair<InetSocketAddress,Integer>> rteTbl;

    private static DatagramSocket sock;
    private static InetSocketAddress myAdr;
    private static InetSocketAddress predecessor; // DHT predecessor
    private static Pair<InetSocketAddress,Integer> myInfo; 
    private static Pair<InetSocketAddress,Integer> predInfo; 
    private static Pair<InetSocketAddress,Integer> succInfo; // successor
    private static Pair<Integer,Integer> hashRange; // my DHT hash range
    private static int sendTag;		// tag for new outgoing packets
    // flag for waiting leave message circle back
    private static boolean stopFlag;
     
    /** Main method for DHT server.
     *  Processes command line arguments, initializes data, joins DHT,
     *  then starts processing requests from clients.
     */
    public static void main(String[] args) {
        // process command-line arguments
        if (args.length < 3) {
            System.err.println("usage: DhtServer myIp numRoutes " +
                       "cfgFile [debug] [ predFile ] ");
            System.exit(1);
        }
        numRoutes = Integer.parseInt(args[1]);
        String cfgFile = args[2];
        cacheOn = debug = false;
        stopFlag = false;
        String predFile = null;
        for (int i = 3; i < args.length; i++) {
            if (args[i].equals("cache")) cacheOn = true;
            else if (args[i].equals("debug")) debug = true;
            else predFile = args[i];
        }
        
        // open socket for receiving packets
        // write ip and port to config file
        // read predecessor's ip/port from predFile (if there is one)
        InetAddress myIp = null; sock = null; predecessor = null;
        try {	
            myIp = InetAddress.getByName(args[0]);
            sock = new DatagramSocket(0,myIp);
            BufferedWriter cfg =
                new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(cfgFile),
                    "US-ASCII"));
            cfg.write("" +	myIp.getHostAddress() + " " +
                    sock.getLocalPort());
            cfg.newLine();
            cfg.close();
            if (predFile != null) {
                BufferedReader pred =
                    new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(predFile),
                        "US-ASCII"));
                String s = pred.readLine();
                String[] chunks = s.split(" ");
                predecessor = new InetSocketAddress(
                    chunks[0],Integer.parseInt(chunks[1]));
            }
        } catch(Exception e) {
            System.err.println("usage: DhtServer myIp numRoutes " +
                       "cfgFile [ cache ] [ debug ] " +
                       "[ predFile ] ");
            System.exit(1);
        }
        myAdr = new InetSocketAddress(myIp,sock.getLocalPort());
        
        // initialize data structures	
        map = new HashMap<String,String>();
        cache = new HashMap<String,String>();
        rteTbl = new LinkedList<Pair<InetSocketAddress,Integer>>();
        sendTag = Math.abs(new Random().nextInt());

        // join the DHT (if not the first node)
        hashRange = new Pair<Integer,Integer>(0, Integer.MAX_VALUE);
        myInfo = new Pair<InetSocketAddress,Integer>(myAdr,0);
        succInfo = null;
        predInfo = null;
        if (predecessor != null) {
            join(predecessor);
        } else {		
            succInfo = new Pair<InetSocketAddress,Integer>(myAdr,0);
            predInfo = new Pair<InetSocketAddress,Integer>(myAdr,0);
        }


        // start processing requests from clients
        Packet p = new Packet();
        Packet reply = new Packet();
        InetSocketAddress sender = null;

        /* this function will be called if there's a "TERM" or "INT"
         * captured by the signal handler. It simply execute the leave
         * function and leave the program.
         */ 
        SignalHandler handler = new SignalHandler() {  
            public void handle(Signal signal) {  
                leave();
                System.exit(0);
            }  
        };
        //Signal.handle(new Signal("KILL"), handler); // capture kill -9 signal
        Signal.handle(new Signal("TERM"), handler); // capture kill -15 signal
        Signal.handle(new Signal("INT"), handler); // capture ctrl+c
        
        while (true) {
            try { sender = p.receive(sock,debug);
            } catch(Exception e) {
                System.err.println("received packet failure");
                continue;
            }
            if (sender == null) {
                System.err.println("received packet failure");
                continue;
            }
            if (!p.check()) {
                reply.clear();
                reply.type = "failure";
                reply.reason = p.reason;
                reply.tag = p.tag;
                reply.ttl = p.ttl;
                reply.clientAdr = p.clientAdr;
                reply.send(sock,sender,debug);
                continue;
            }
            handlePacket(p,sender);
        }
    }

    /** Hash a string, returning a 32 bit integer.
     *  @param s is a string, typically the key from some get/put operation.
     *  @return and integer hash value in the interval [0,2^31).
     */
    public static int hashit(String s) {
        while (s.length() < 16) s += s;
        byte[] sbytes = null;
        try { sbytes = s.getBytes("US-ASCII"); 
        } catch(Exception e) {
            System.out.println("illegal key string");
            System.exit(1);
        }
        int i = 0;
        int h = 0x37ace45d;
        while (i+1 < sbytes.length) {
            int x = (sbytes[i] << 8) | sbytes[i+1];
            h *= x;
            int top = h & 0xffff0000;
            int bot = h & 0xffff;
            h = top | (bot ^ ((top >> 16)&0xffff));
            i += 2;
        }
        if (h < 0) h = -(h+1);
        return h;
    }

    /** Leave an existing DHT.
     *  
     *	Send a leave packet to it's successor and wait until stopFlag is 
     * 	set to "true", which means leave packet is circle back.
     *
     *	Send an update packet with the new hashRange and succInfo fields to 
     *  its predecessor, and sends an update packet with the predInfo 
     *  field to its successor. 
     *	
     *	Transfers all keys and values to predecessor.  
     *	Clear all the existing cache, map and rteTbl information
     */
    public static void leave() {
        Packet leave_p = new Packet();
        leave_p.type = "leave";
        leave_p.tag = sendTag;
        leave_p.senderInfo = myInfo;
        leave_p.send(sock, succInfo.left, debug);
        while (!stopFlag);
        leave_p.clear();
        leave_p.type = "update";
        leave_p.succInfo = succInfo;

        int newleft = predInfo.right;
        Pair<Integer, Integer> newRange = new Pair<Integer, Integer>(newleft, hashRange.right);
        leave_p.hashRange = newRange;
        leave_p.tag = sendTag;
        leave_p.send(sock, predInfo.left, debug);

        leave_p.clear();
        leave_p.type = "update";
        leave_p.predInfo = predInfo;
        leave_p.tag = sendTag;
        leave_p.send(sock, succInfo.left, debug);
        
        leave_p.clear();
        leave_p.type = "transfer";
        leave_p.tag = sendTag;
        for (Map.Entry<String, String> entry: map.entrySet()) {
            leave_p.key = entry.getKey();
            leave_p.val = entry.getValue();
            leave_p.send(sock, predInfo.left, debug);
        }
        map.clear();
        rteTbl.clear();
        if(cacheOn) cache.clear();
    }
    
    /** Handle a update packet from a prospective DHT node.
     *  @param p is the received join packet
     *  @param adr is the socket address of the host that
     *  
     *	The update message might contains infomation need update,
     *	including predInfo, succInfo, and hashRange. 
     *  And add the new Predecessor/Successor into the routing table.
     *	If succInfo is updated, succInfo should be removed from 
     *	the routing table and the new succInfo should be added
     *	into the new routing table.
     */
    public static void handleUpdate(Packet p, InetSocketAddress adr) {
        if (p.predInfo != null) {
            predInfo = p.predInfo;
        }
        if (p.succInfo != null){
            succInfo = p.succInfo;
            addRoute(succInfo);
        }
        if (p.hashRange != null){
            hashRange = p.hashRange;
        }
    }

    /** Handle a leave packet from a leaving DHT node.
    *  @param p is the received join packet
    *  @param adr is the socket address of the host that sent the leave packet
    *
    *  If the leave packet is sent by this server, set the stopFlag.
    *  Otherwise firstly send the received leave packet to its successor,
    *  and then remove the routing entry with the senderInfo of the packet.
    */
    public static void handleLeave(Packet p, InetSocketAddress adr) {
        if (p.senderInfo.equals(myInfo)){
            stopFlag = true;
            return;
        }
        // send the leave message to successor 
        p.send(sock, succInfo.left, debug);

        //remove the senderInfo from route table
        removeRoute(p.senderInfo);
    }
    
    /** Join an existing DHT.
     *  @param predAdr is the socket address of a server in the DHT,
     *  
     *  The current server acts as an incoming server, it
     *	sends a join packet to its predecessor
     */
    public static void join(InetSocketAddress predAdr) {
        Packet join_p = new Packet();
        join_p.type = "join";
        join_p.tag = sendTag;
        join_p.send(sock, predAdr, debug);
    }
    
    /** Handle a join packet from a prospective DHT node.
     *  @param p is the received join packet
     *  @param succAdr is the socket address of the host that
     *  sent the join packet (the new successor)
     *
     *  calculate the hashRange for the incoming server
     *  send an update packet to the successor
     *  send a success packet to the coming server and add to the route table
     *  transfer the corresponding (key, value) pairs to the incoming server
     */
    public static void handleJoin(Packet p, InetSocketAddress succAdr) {
        int len = hashRange.right - hashRange.left;
        int newleft = hashRange.left + (len / 2 + 1);
        int tag = p.tag;
        Pair<Integer, Integer> newRange = new Pair<Integer, Integer>(newleft, hashRange.right);
        Pair<InetSocketAddress, Integer> newInfo = new Pair<InetSocketAddress, Integer>(succAdr, newleft);
        
        p.clear();
        p.type = "update";
        p.senderInfo = myInfo;
        p.predInfo = newInfo;
        p.tag = sendTag;
        p.send(sock, succInfo.left, debug);
        
        p.clear();
        p.type = "success";
        p.succInfo = succInfo;
        p.predInfo = myInfo;
        p.hashRange = newRange;
        p.tag = tag;
        p.send(sock, succAdr, debug);

        succInfo = newInfo;
        hashRange.right = newleft - 1;
        addRoute(succInfo);
        
        p.clear();
        p.type = "transfer";
        p.tag = sendTag;
        List<String> toRemove = new ArrayList<String>();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            int hashval = hashit(entry.getKey());
            if(hashval >= newRange.left && hashval <= newRange.right) {
                p.key = entry.getKey();
                p.val = entry.getValue();
                p.send(sock, succAdr, debug);
                toRemove.add(p.key);
            }
        }
        for(String key : toRemove) map.remove(key);
    }
    
    /** Handle a get packet.
     *  @param p is a get packet
     *  @param senderAdr is the the socket address of the sender
     *
     *  check the cacheOn and cache, if it has the key, send back directly
     *  if the server is responsible for the key, find it and send 
     *  success/no match packet to the relay server
     *  else forward to the server which has the closest range to the key.
     */
    public static void handleGet(Packet p, InetSocketAddress senderAdr) {
        InetSocketAddress replyAdr;
        int hash = hashit(p.key);
        int left = hashRange.left.intValue();
        int right = hashRange.right.intValue();

        if(cacheOn && cache.containsKey(p.key)) {
            if (p.relayAdr != null) {
                replyAdr = p.relayAdr;
                p.senderInfo = myInfo;
            } else {
                replyAdr = senderAdr;
            }
            p.type = "success"; p.val = cache.get(p.key);
            p.send(sock,replyAdr,debug);
        }
        else {
            if (left <= hash && hash <= right) {
                // respond to request using map
                if (p.relayAdr != null) {
                    replyAdr = p.relayAdr;
                    p.senderInfo = myInfo;
                } else {
                    replyAdr = senderAdr;
                }
                if (map.containsKey(p.key)) {
                    p.type = "success"; p.val = map.get(p.key);
                } else {
                    p.type = "no match";
                }
                p.send(sock,replyAdr,debug);
            } else {
                // forward around DHT
                if (p.relayAdr == null) {
                    p.relayAdr = myAdr; p.clientAdr = senderAdr;
                }
                forward(p,hash);
            }
        }
    }
    
    /** Handle a put packet.
     *  @param p is a put packet
     *  @param senderAdr is the the socket address of the sender
     *  
     *	check the cacheOn and cache, if containing the key, remove it from the cache
     *  if the server is responsible for the key, put it into the map
     *  else forward to another server.
     */
    public static void handlePut(Packet p, InetSocketAddress senderAdr) {
        if(cacheOn && cache.containsKey(p.key)) cache.remove(p.key);
        InetSocketAddress replyAdr;
        int hash = hashit(p.key);
        int left = hashRange.left.intValue();
        int right = hashRange.right.intValue();

        if (left <= hash && hash <= right) {
            // respond to request using map
            if (p.relayAdr != null) {
                replyAdr = p.relayAdr;
                p.senderInfo = myInfo;
            } else {
                replyAdr = senderAdr;
            }
            p.type = "success";
            map.put(p.key,p.val);
            p.send(sock, replyAdr, debug);
        } else {
            // forward around DHT
            if (p.relayAdr == null) {
                p.relayAdr = myAdr; p.clientAdr = senderAdr;
            }
            forward(p,hash);
        }
    }

    /** Handle a transfer packet.
     *  @param p is a transfer packet
     *  @param senderAdr is the the address (ip:port) of the sender
     *  
     *	when receiving a tranfer packet, put it into the map.
     */
    public static void handleXfer(Packet p, InetSocketAddress senderAdr) {
        map.put(p.key,p.val);
    }
    
    /** Handle a reply packet.
     *  @param p is a reply packet, more specifically, a packet of type
     *  "success", "failure" or "no match"
     *  @param senderAdr is the the address (ip:port) of the sender
     *  
     *  if type is success, there are 2 cases. 
     *  one is that the current server is joining DHT, then update corresponding info
     *	the other one is success packet for get/put request, then send back to client
     *	if the type is no match or failure, send back to client.
     */
    public static void handleReply(Packet p, InetSocketAddress senderAdr) {
        if(p.type.equals("success")) {
            if(p.hashRange != null) {
                succInfo = p.succInfo;
                predInfo = p.predInfo;
                hashRange = p.hashRange;
                myInfo.right = hashRange.left;
                addRoute(succInfo);
            }
            else if (p.key != null) {
                InetSocketAddress cAdr = p.clientAdr;
                p.senderInfo = null;
                p.clientAdr = null;
                p.relayAdr = null;
                if(cacheOn) cache.put(p.key, p.val);
                p.send(sock,cAdr,debug);
            }
        }
        else if (p.type.equals("no match") || p.type.equals("failure")) {
            InetSocketAddress cAdr = p.clientAdr;
            p.senderInfo = null;
            p.clientAdr = null;
            p.relayAdr = null;
            p.send(sock,cAdr,debug);
        }
    }
    
    /** Handle packets received from clients or other servers
     *  @param p is a packet
     *  @param senderAdr is the address (ip:port) of the sender
     */
    public static void handlePacket(Packet p, InetSocketAddress senderAdr) {
        if (p.senderInfo != null & !p.type.equals("leave"))
            addRoute(p.senderInfo);
        if (p.type.equals("get")) {
            handleGet(p,senderAdr);
        } else if (p.type.equals("put")) {
            handlePut(p, senderAdr);
        } else if (p.type.equals("transfer")) {
            handleXfer(p, senderAdr);
        } else if (p.type.equals("success") ||
               p.type.equals("no match") ||
                    p.type.equals("failure")) {
            handleReply(p, senderAdr);
        } else if (p.type.equals("join")) {
            handleJoin(p, senderAdr);
        } else if (p.type.equals("update")){
            handleUpdate(p, senderAdr);
        } else if (p.type.equals("leave")){
            handleLeave(p, senderAdr);
        }
    }
    
    /** Add an entry to the route tabe.
     *  @param newRoute is a pair (addr,hash) where addr is the socket
     *  address for some server and hash is the first hash in that
     *  server's range
     *
     *  If the number of entries in the table exceeds the max
     *  number allowed, the first entry that does not refer to
     *  the successor of this server, is removed.
     *  If debug is true and the set of stored routes does change,
     *  print the string "rteTbl=" + rteTbl. (IMPORTANT)
     */
    public static void addRoute(Pair<InetSocketAddress,Integer> newRoute){
        // entry equals to myself 
        if(myInfo.equals(newRoute)) return;

        // entry already exists
        for(int i = 0; i < rteTbl.size(); i++) {
            if(rteTbl.get(i).equals(newRoute)) {
                return;
            }
        }
        boolean can_add = true;
        if(rteTbl.size() == numRoutes) {
            // table is full
            can_add = false;
            for(int i = 0; i < rteTbl.size(); i++) {
                if(!rteTbl.get(i).equals(succInfo)) {
                    rteTbl.remove(i);
                    can_add = true;
                    break;
                }
            }
        }
        if(can_add) {
            // table content does change
            rteTbl.add(newRoute);
            if(debug) System.out.println("rteTbl=" + rteTbl);
        }
    }

    /** Remove an entry from the route tabe.
     *  @param rmRoute is the route information for some server 
     *  need to be removed from route table
     *
     *  If the route information exists in current entries, remove it.
     *	Otherwise, do nothing.
     *  If debug is true and the set of stored routes does change,
     *  print the string "rteTbl=" + rteTbl. (IMPORTANT)
     */
    public static void removeRoute(Pair<InetSocketAddress,Integer> rmRoute){
        for(int i = 0; i < rteTbl.size(); i++) {
            if(rteTbl.get(i).equals(rmRoute)) {
                rteTbl.remove(i);
                if(debug) System.out.println("rteTbl=" + rteTbl);
                return;
            }
        }
    }


    /** Forward a packet using the local routing table.
     *  @param p is a packet to be forwarded
     *  @param hash is the hash of the packet's key field
     *
     *  This method selects a server from its route table that is
     *  "closest" to the target of this packet (based on hash).
     *  If firstHash is the first hash in a server's range, then
     *  we seek to minimize the difference hash-firstHash, where
     *  the difference is interpreted modulo the range of hash values.
     *  IMPORTANT POINT - handle "wrap-around" correctly. 
     *  Once a server is selected, p is sent to that server.
     */
    public static void forward(Packet p, int hash) {
        // pair<dest_server_addr, current_min_distance>
        Pair<InetSocketAddress, Integer> target = new Pair<InetSocketAddress, Integer>(null, Integer.MAX_VALUE);
        for(Pair<InetSocketAddress, Integer> route : rteTbl) {
            int dist = hash - route.right;
            if(dist < 0) dist += Integer.MAX_VALUE;
            if(dist < target.right) {
                target.left = route.left;
                target.right = dist;
            }
        }
        p.send(sock, target.left, debug);
    }
}
