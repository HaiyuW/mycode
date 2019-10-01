import java.io.*;
import java.net.*;
import java.util.*;

/** TCP Map Server 
 *  author: Haiyu Wang and Haodong Huang
 * 
 *  usage: java TcpMapServer [IP address] [port number]
 * 
 *  have a storage service for 4 operations: get, put, remove, get all
 *  IP address and port number are optional. Default IP address is
 *  wildcard and default port number is 30123. 
 *  
 *  If port number is specified, IP address must also be specified.
 * 
 *  Use Jon Turner's program - TCP echo server (7/2013) as a starting point
 */


public class TcpMapServer {
	public static void main(String args[]) throws Exception {

		// create HashMap
		HashMap<String, String> myMap = new HashMap<String, String>();

		// process arguments
		int port = 30123;
		if (args.length > 1) port = Integer.parseInt(args[1]);
		InetAddress ipAdr = null;
		if (args.length > 0) ipAdr = InetAddress.getByName(args[0]);

		// create and bind listening socket
		ServerSocket listenSock = new ServerSocket(port,0,ipAdr);

		while (true) {
			// wait for incoming connection request and
			// create new socket to handle it (connection Socket)
			Socket connSock = listenSock.accept();
	
			// create reader & writer socket's in/out streams
			BufferedReader  in = new BufferedReader(new InputStreamReader(
				    	 connSock.getInputStream(),"US-ASCII"));
			BufferedOutputStream out = new BufferedOutputStream(
						   connSock.getOutputStream());
;

			while (true) {

				String str;
				str = in.readLine();
				if (str == null || str.length() == 0) break;

				// split the string with ":"
				String[] strSplit = str.split(":");

				// initiate value
				String value = "";

				switch (strSplit[0]){

					// Operation Get
					case "get":{
						if (strSplit.length!=2){
							value = "error: unrecognizable input: " + str + "\n"; 
						}
						else{	
							value = myMap.get(strSplit[1]);
							if (value == null)
								value = "no match\n";
							else
								value = "Ok:"+value+"\n";
						}
						break;
					}

					// Operation Put
					case "put":{
						if (strSplit.length!=3){
							value = "error: unrecognizable input: " + str + "\n"; 
						}
						else{
							if (myMap.containsKey(strSplit[1]))
								value = "Updated:"+strSplit[1]+"\n";
							else
								value = "Ok\n";
							myMap.put(strSplit[1],strSplit[2]);
						}
						break;
					}

					// Operation Remove
					case "remove":{
						if (strSplit.length!=2){
							value = "error: unrecognizable input: " + str + "\n"; 
						}
						else {
							if (myMap.containsKey(strSplit[1])){
								myMap.remove(strSplit[1]);
								value = "Ok\n";
							}
							else
								value = "no match\n";
						}
						break;
					}

					// Operation GetAll
					case "get all":{
						if (strSplit.length!=1){
							value = "error: unrecognizable input: " + str + "\n"; 
						}
						else{
							for (Map.Entry<String, String> entry : myMap.entrySet()) {
								value += entry.getKey() + ":" + entry.getValue() + "::";
							}
							value = value.substring(0,value.length()-2); 
							value += '\n';
						}
						break;
					}

					default: {
						value = "error: unrecognizable input: " + str + "\n";
						break;
					}

					// since client use readLine(), value should be end with '\n'
				}
				// write value to socket
				out.write(value.getBytes()); out.flush();
			}
			connSock.close();
		}
	}
}
