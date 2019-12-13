import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/** Router module for an overlay router.
 *  
 *  This class implements a simplified overlay IP router. It runs as a separate thread.
 * 
 *  The router maintains routing information in its own routing table by sending 
 *  hello packets and advertisement packets to its neighbors periodically.
 *  Link information is maintained through sending and receiving hello packets.
 * 
 *  When the router receives a packet from the forwarder, it handles the packet
 *  based on the type of the packet which is written in the payload.
 *  Basically there are 4 types of packets to handle:
 *  hello, reply to hello, advertisement, failure advertisement.
 *  
 *  When there are changes of validity or output link in any route in the routing table,
 *  the router updates the forwarder's forwarding table correspondingly.
 */
public class Router implements Runnable {
    private Thread myThread;		// thread that executes run() method
    private int myIp;			// ip address in the overlay
    private String myIpString;		// String representation
    private ArrayList<Prefix> pfxList;	// list of prefixes to advertise
    private ArrayList<NborInfo> nborList;	// list of info about neighbors
    private class LinkInfo { 		// class used to record link information
        public int peerIp;		// IP address of peer in overlay net
        public double cost;		// in seconds
        public boolean gotReply;	// flag to detect hello replies
        public int helloState;		// set to 3 when hello reply received
                        // decremented whenever hello reply
                        // is not received; when 0, link is down

        // link cost statistics
        public int count;
        public double totalCost;
        public double minCost;
        public double maxCost;

        LinkInfo() {
            cost = 0; gotReply = true; helloState = 3;
            count = 0; totalCost = 0; minCost = 10; maxCost = 0;
        }
    }
    private ArrayList<LinkInfo> lnkVec;     // indexed by link number

    private class Route { 		        // routing table entry
        public Prefix pfx;	        // destination prefix for route
        public double timestamp;        // time this route was generated
        public double cost;	        // cost of route in ns
        public LinkedList<Integer> path;// list of router IPs;
                        // destination at end of list
        public int outLink;		// outgoing link for this route
        public boolean valid;		//indicate the valid of the route
        public String toString() {
            String res = String.format("%s %.3f %.3f", pfx, timestamp, cost);
            for(int path_ip : path) res += " " + Util.ip2string(path_ip);
            return res;
        }
    }
    private ArrayList<Route> rteTbl;        // routing table

    private Forwarder fwdr;		        // reference to Forwarder object

    private double now;								// current time in ns
    private static final double sec = 1000000000; 	// ns per sec

    private int debug;		// controls debugging output
    private boolean quit;		// stop thread when true
    private boolean enFA;		// link failure advertisement enable


    /** Initialize a new Router object.
     *  
     *  @param myIp is an integer representing the overlay IP address of
     *  this node in the overlay network
     *  @param fwdr is a reference to the Forwarder object through which
     *  the Router sends and receives packets
     *  @param pfxList is a list of prefixes advertised by this router
     *  @param nborList is a list of neighbors of this node
     *
     *  @param debug is an integer that controls the amount of debugging
     *  information that is to be printed
     */

    Router(int myIp, Forwarder fwdr, ArrayList<Prefix> pfxList,
            ArrayList<NborInfo> nborList, int debug, boolean enFA) {
        this.myIp = myIp; this.myIpString = Util.ip2string(myIp);
        this.fwdr = fwdr; this.pfxList = pfxList;
        this.nborList = nborList; this.debug = debug;
        this.enFA = enFA;

        lnkVec = new ArrayList<LinkInfo>();
        for (NborInfo nbor : nborList) {
            LinkInfo lnk = new LinkInfo();
            lnk.peerIp = nbor.ip;
            lnk.cost = nbor.delay;
            lnkVec.add(lnk);
        }
        rteTbl = new ArrayList<Route>();
        quit = false;
    }

    /** Instantiate and start a thread to execute run(). */
    public void start() {
        myThread = new Thread(this); myThread.start();
    }

    /** Terminate the thread. */
    public void stop() throws Exception { quit = true; myThread.join(); }

    /** This is the main thread for the Router object.
     *
     *  The router sends hello packets to all of its neighbors every 1 second
     *  and sends advertisement every 10 seconds.
     * 
     *  It also receives packets from the forwarder and handles them based on 
     *  the value of type field written in the payload. During the handling process,
     *  the router updates its routing table and the forwarder's forwarding table
     *  whenever there are appropriate changes.
     * 
     *  After the stop signal arrives, the main loop terminates and print 
     *  link status information.
     */
    public void run() {
        double t0 = System.nanoTime()/sec;
        now = 0;
        double helloTime, pvSendTime;
        helloTime = pvSendTime = now;
        while (!quit) {
            now = System.nanoTime()/sec - t0;
            boolean nothing = true;
            if(now - helloTime >= 1.0) {
                // if it's time to send hello packets, do it
                sendHellos();
                helloTime = now;
                nothing = false;
            }
            if(now - pvSendTime >= 10.0) {
                // else if it's time to send advertisements, do it
                sendAdverts();
                pvSendTime = now;
                nothing = false;
            }
            if(fwdr.incomingPkt()) {
                // else if the forwarder has an incoming packet
                // to be processed, retrieve it and process it
                handleIncoming();
                nothing = false;
            }
            if(nothing) {
                // else nothing to do, so take a nap
                try {
                    Thread.sleep(1);
                } catch(Exception e) {
                    System.err.println("Router:run: "
                        + "can't sleep " + e);
                    System.exit(1);
                }
            }
        }
        String s = String.format("Router link cost statistics\n" + 
            "%8s %8s %8s %8s %8s\n","peerIp","count","avgCost",
            "minCost","maxCost");
        for (LinkInfo lnk : lnkVec) {
            if (lnk.count == 0) continue;
            s += String.format("%8s %8d %8.3f %8.3f %8.3f\n",
                Util.ip2string(lnk.peerIp), lnk.count,
                lnk.totalCost/lnk.count,
                lnk.minCost, lnk.maxCost);
        }
        System.out.println(s);
    }

    /** Lookup route in routing table.
     *
     * @param pfx is IP address prefix to be looked up.
     * @return a reference to the Route that matches the prefix or null
     */
    private Route lookupRoute(Prefix pfx) {
        for(Route r : rteTbl) {
            if(r.pfx.equals(pfx)) return r;
        }
        return null;
    }

    /** Add a route to the routing table.
     * 
     *  @param rte is a route to be added to the table; no check is
     *  done to make sure this route does not conflict with an existing
     *  route
     */
    private void addRoute(Route r) {
        Route rte = new Route();
        rte.pfx = new Prefix(r.pfx.toString());
        rte.timestamp = now;
        rte.cost = r.cost;
        rte.path = new LinkedList<Integer>();
        for(int i : r.path) rte.path.add(i);
        rte.outLink = r.outLink;
        rte.valid = r.valid;
        rteTbl.add(rte);
    }

     /** Update a route in the routing table.
     *
     *  @param rte is a reference to a route in the routing table.
     *  @param nuRte is a reference to a new route that has the same
     *  prefix as rte
     *  @return true if rte is modified, else false
     *
     *  This method replaces certain fields in rte with fields
     *  in nuRte. Specifically,
     *
     *  if nuRte has a link field that refers to a disabled
     *  link, ignore it and return false
     *
     *  else, if the route is invalid, then update the route
     *  and return true,
     *
     *  else, if both routes have the same path and link,
     *  then the timestamp and cost fields of rte are updated
     *
     *  else, if nuRte has a cost that is less than .9 times the
     *  cost of rte, then all fields in rte except the prefix fields
     *  are replaced with the corresponding fields in nuRte
     *
     *  else, if nuRte is at least 20 seconds newer than rte
     *  (as indicated by their timestamps), then all fields of
     *  rte except the prefix fields are replaced
     *
     *  else, if the link field for rte refers to a link that is
     *  currently disabled, replace all fields in rte but the
     *  prefix fields
     */
    private boolean updateRoute(Route rte, Route nuRte) {
        if(lnkVec.get(nuRte.outLink).helloState == 0) return false;
        if(!rte.valid) {
            rte.cost = nuRte.cost;
            rte.path = new LinkedList<Integer>();
            for(int i : nuRte.path) rte.path.add(i);
            rte.outLink = nuRte.outLink;
            rte.valid = nuRte.valid;
            rte.timestamp = now;
            return true;
        }
        if(rte.path.equals(nuRte.path) && rte.outLink == nuRte.outLink) {
            rte.cost = nuRte.cost;
            rte.timestamp = now;
            return true;
        }
        if(nuRte.cost < 0.9 * rte.cost 
            || nuRte.timestamp > 20 + rte.timestamp 
            || lnkVec.get(rte.outLink).helloState == 0){
            rte.cost = nuRte.cost;
            rte.path = new LinkedList<Integer>();
            for(int i : nuRte.path) rte.path.add(i);
            rte.outLink = nuRte.outLink;
            rte.valid = nuRte.valid;
            rte.timestamp = now;
            return true;
        }
        return false;
    }
                
    /** Send hello packet to all neighbors.
     *
     *  First check for replies. If no reply received on some link,
     *  update the link status by subtracting 1. If that makes it 0,
     *  the link is considered down, so we mark all routes using 
     *  that link as invalid. Also, if certain routes are marked as 
     *  invalid, we will need to print the table if debug larger 
     *  than 1, and we need to send failure advertisement by 
     *  calling sendFailureAdvert if failure advertisement is enable.
     */
    public void sendHellos() {
        int lnk = 0;
        for (LinkInfo lnkInfo : lnkVec) {
            if(!lnkInfo.gotReply && lnkInfo.helloState > 0) {
                // if no reply to the last hello, subtract 1 from
                // link status if it's not already 0
                lnkInfo.helloState--;
                if(lnkInfo.helloState == 0) {
                    // go through the routes to check routes 
                    // that contain the failed link
                    boolean changed = false;
                    for(Route r : rteTbl) {
                        if(r.outLink == lnk && r.valid) {
                            changed = true;
                            r.valid = false;
                            fwdr.addRoute(r.pfx, -1);
                        }
                    }
                    // print routing table if debug is enabled 
                    // and valid field of route is changed
                    if(debug > 0 && changed) printTable();

                    // send link failure advertisement if enFA is enabled
                    // and valid field of route is changed
                    if(enFA && changed) sendFailureAdvert(lnk);
                }
            }
            // send new hello, after setting gotReply to false
            lnkInfo.gotReply = false;
            Packet p = new Packet();
            p.srcAdr = myIp; p.destAdr = lnkInfo.peerIp;
            p.protocol = 2; p.ttl = 100;
            p.payload = String.format("RPv0\ntype: hello\ntimestamp: %.3f\n", now);
            if(fwdr.ready4pkt()) fwdr.sendPkt(p, lnk);
            lnk++;
        }
    }

    /** Send initial path vector to each of our neighbors.  */
    public void sendAdverts() {
        for(Prefix pfx : pfxList) {
            extend(-1, String.format("RPv0\ntype: advert\npathvec: %s %.3f %.3f %s",
                    pfx.toString(), now, 0.0, myIpString));
        }
    }


    /** Send link failure advertisement to all available neighbors
     *
     *  @param failedLnk is the number of link on which is failed.
     *
     */
    public void sendFailureAdvert(int failedLnk){
        int failIp = lnkVec.get(failedLnk).peerIp;
        String failIpString = Util.ip2string(failIp);

        for (int lnk = 0; lnk < nborList.size(); lnk++) {
            if (lnkVec.get(lnk).helloState == 0) continue;
            Packet p = new Packet();
            p.protocol = 2; p.ttl = 100;
            p.srcAdr = myIp;
            p.destAdr = lnkVec.get(lnk).peerIp;
            p.payload = String.format("RPv0\ntype: fadvert\n"
                + "linkfail: %s %s %.3f %s\n",
                myIpString,  failIpString, now, myIpString);
            fwdr.sendPkt(p,lnk);
        }
    }

    /** Retrieve and process packet received from Forwarder.
     *
     *  For hello packets, we simply echo them back.
     *  For replies to our own hello packets, we update costs.
     *  For advertisements, we update routing state and propagate
     *  as appropriate.
     */
    public void handleIncoming() {
        // parse the packet payload
        Pair<Packet,Integer> pp = fwdr.receivePkt();
        Packet p = pp.left; int lnk = pp.right;

        String[] lines = p.payload.split("\n");
        if (!lines[0].equals("RPv0")) return;

        String[] chunks = lines[1].split(":");
        if (!chunks[0].equals("type")) return;
        String type = chunks[1].trim();

        // if it's an route advert, call handleAdvert
        if(type.equals("advert")) handleAdvert(lines, lnk); 

        // if it's a link failure advert, call handleFailureAdvert
        else if(type.equals("fadvert")) handleFailureAdvert(lines, lnk);

        // if it's a hello, echo it back
        else if(type.equals("hello")) {
            p.destAdr = p.srcAdr;
            p.srcAdr = myIp;
            p.payload = String.format("RPv0\ntype: hello2u\ntimestamp: %s\n", lines[2].split(":")[1].trim());
            fwdr.sendPkt(p, lnk);
        }
        else {
            // else it's a reply to a hello packet
            // use timestamp to determine round-trip delay
            double RTT = (now - Double.parseDouble(lines[2].split(":")[1].trim())) / 2;

            // use this to update the link cost using exponential
            // weighted moving average method
            lnkVec.get(lnk).cost = lnkVec.get(lnk).cost * 0.9 + RTT * 0.1;
            
            // also, update link cost statistics
            lnkVec.get(lnk).count++;
            lnkVec.get(lnk).totalCost += RTT;
            lnkVec.get(lnk).minCost = Math.min(lnkVec.get(lnk).minCost, RTT);
            lnkVec.get(lnk).maxCost = Math.max(lnkVec.get(lnk).maxCost, RTT);

            // also, set gotReply to true            
            lnkVec.get(lnk).gotReply = true;
            lnkVec.get(lnk).helloState = 3;
        }
    }

    /** Handle an advertisement received from another router.
     *
     *  @param lines is a list of lines that defines the packet;
     *  the first two lines have already been processed at this point
     *
     *  @param lnk is the number of link on which the packet was received
     */
    private void handleAdvert(String[] lines, int lnk) {
        // example path vector line
        // pathvec: 1.2.0.0/16 345.678 .052 1.2.0.1 1.2.3.4
        
        String[] chunks = lines[2].split(":")[1].trim().split(" ");

        // Form a new route, with cost equal to path vector cost
        // plus the cost of the link on which it arrived.
        Route nuRoute = new Route();
        nuRoute.pfx = new Prefix(chunks[0]);
        nuRoute.timestamp = Double.parseDouble(chunks[1]);
        nuRoute.cost = Double.parseDouble(chunks[2]) + lnkVec.get(lnk).cost;

        // Parse the path vector line.
        // If there is loop in path vector, ignore this packet.
        nuRoute.path = new LinkedList<Integer>();
        for(int i = 3; i < chunks.length; i++) {
            int nuIp = Util.string2ip(chunks[i]);
            if(myIp == nuIp) return;
            nuRoute.path.add(nuIp);
        }
        nuRoute.outLink = lnk;
        nuRoute.valid = true;

        // Look for a matching route in the routing table
        Route r = lookupRoute(nuRoute.pfx);
        if(r != null) {
            boolean path_same = r.path.equals(nuRoute.path);
            boolean link_same = (r.outLink == nuRoute.outLink);
            if(updateRoute(r, nuRoute)) {
                // and update as appropriate; whenever an update
                // changes the path, print the table if debug>0;    
                if(!path_same && debug > 0) printTable();

                // whenever an update changes the output link,
                // update the forwarding table as well.
                if(!link_same) fwdr.addRoute(r.pfx, r.outLink);

                // If the new route changed the routing table,
                // extend the path vector and send it to other neighbors
                nuRoute.path.add(0, myIp);
                extend(lnk, String.format("RPv0\ntype: advert\npathvec: %s\n", nuRoute.toString()));
            }
        }
        else {
            addRoute(nuRoute);
            printTable();
            fwdr.addRoute(nuRoute.pfx, nuRoute.outLink);
            nuRoute.path.add(0, myIp);
            extend(lnk, String.format("RPv0\ntype: advert\npathvec: %s\n", nuRoute.toString()));
        }
    }

    /** Handle the failure advertisement received from another router.
     *
     *  @param lines is a list of lines that defines the packet;
     *  the first two lines have already been processed at this point
     *
     *  @param lnk is the number of link on which the packet was received
     */
    private void handleFailureAdvert(String[] lines, int lnk) {
        // example path vector line
        // fadvert: 1.2.0.1 1.3.0.1 345.678 1.4.0.1 1.2.0.1
        // meaning link 1.2.0.1 to 1.3.0.1 is failed
        
        String[] chunks = lines[2].split(":")[1].trim().split(" ");
        String path = "";

        // Parse the path vector line.
        // If there is loop in path vector, ignore this packet.
        for(int i = 3; i < chunks.length; i++) {
            if(myIpString == chunks[i]) return;
            path += " " + chunks[i];
        }
        int firstIp = Util.string2ip(chunks[0]);
        int secondIp = Util.string2ip(chunks[1]);
        boolean changed = false;

        // go through routes to check if it contains the link
        // set the route as invalid (false) if it does
        for(Route r : rteTbl) {
            for(int i = 0; i < r.path.size(); i++) {
                if(i + 1 < r.path.size() && r.path.get(i) == firstIp && r.path.get(i+1) == secondIp) {
                    // update the time stamp if route is changed
                    r.timestamp = now;
                    r.valid = false;
                    changed = true;
                    fwdr.addRoute(r.pfx, -1);
                }
            }
        }
        if(changed) {
            // print route table if route is changed and debug is enabled
            if(debug > 0) printTable();

            // If one route is changed, extend the message 
            // and send it to other neighbors.
            extend(lnk, String.format("RPv0\ntype: fadvert\nlinkfail: %s %s %s %s%s\n", 
                    Util.ip2string(firstIp), Util.ip2string(secondIp), chunks[2], myIpString, path));
        }
        
    }

    /** 
     * Extend an advertisement to all of the router's neighbors through its incident links
     * except the link on which this advertisement arrives
     * 
     * @param fromLnk is the link number of the link on which the advertisement arrives
     * Value of -1 means no ancestor link and is used in sending hello/advertisement packets
     * originated by the router
     * 
     * @param payload is the value of payload field of the packet to be sent
    */
    private void extend(int fromLnk, String payload) {
        for(int i = 0; i < nborList.size(); i++) {
            if(i == fromLnk || lnkVec.get(i).helloState == 0) continue;
            Packet p = new Packet();
            p.srcAdr = myIp; p.destAdr = nborList.get(i).ip;
            p.protocol = 2; p.ttl = 100;
            p.payload = payload;
            fwdr.sendPkt(p, i);
        }
    }

    /** Print the contents of the routing table. */
    public void printTable() {
        String s = String.format("Routing table (%.3f)\n"
            + "%10s %10s %8s %5s %10s \t path\n", now, "prefix", 
            "timestamp", "cost","link", "VLD/INVLD");
        for (Route rte : rteTbl) {
            s += String.format("%10s %10.3f %8.3f",
                rte.pfx.toString(), rte.timestamp, rte.cost);
            
            s += String.format(" %5d", rte.outLink);
            
            if (rte.valid == true)
                s+= String.format(" %10s", "valid");
            else
                s+= String.format(" %10s \t", "invalid");
            
            for (int r :rte.path)
                s += String.format (" %s",Util.ip2string(r));
            
            if (lnkVec.get(rte.outLink).helloState == 0)
                s += String.format("\t ** disabled link");
            s += "\n";
        }
        System.out.println(s);
    }
}
