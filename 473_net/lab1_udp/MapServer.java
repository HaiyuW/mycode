import java.io.*;
import java.net.*;
import java.util.*;

/**
 * author: Haiyu Wang
 * usage: java MapServer
 * 
 * have a simple storage servive with three operations: get, put,remove
 * receive a packet from client and decide the operation
 * send back certain packet back to client
**/

public class MapServer {
	public static void main(String args[]) throws Exception {

		// create Hashmap as storage service
		HashMap<String,String> Map = new HashMap<String,String>();

		//get address and port 
		InetAddress myIp = null; 
		int port = 30123;
		if (args.length > 0) myIp = InetAddress.getByName(args[0]);
		if (args.length > 1) port = Integer.parseInt(args[1]);

		//create socket
		DatagramSocket sock = new DatagramSocket(port,myIp);

		// create incoming packets
		byte[] buf = new byte[1000];
		DatagramPacket inpkt = new DatagramPacket(buf, buf.length);

		while (true) {
			// wait for incoming packet
			sock.receive(inpkt);

			// get strings from buf and split them
			String str = new String(buf,0,inpkt.getLength(),
							"US-ASCII");
			String[] strSplit = str.split(":");

			//get client address from incoming packet
			SocketAddress clientAdr = inpkt.getSocketAddress();

			String value = null;

			// check the first string and decide the operation
			switch (strSplit[0]){

				// operation get
				case "get": {
					// check the format
					if (strSplit.length>2){
						value = "Error:unrecognizable input: "
							+"put a copy of the input's"
							+" packet payload here";

					}
					else{
						value = Map.get(strSplit[1]);
						if (value == null)
							value = "no match";
						else
							value = "Ok:"+value;
					}
					break;
				}
				
				// operation put	
				case "put": {
					// check the format
					if (strSplit.length>3){
						value = "Error:unrecognizable input: "
							+"put a copy of the input's"
							+" packet payload here";
					}
					else{
						if (Map.containsKey(strSplit[1]))
							value = "updated:"+strSplit[1];
						else
							value = "Ok";
						Map.put(strSplit[1],strSplit[2]);
					}
					break;
				}

				//operation remove
				case "remove": {
					// check the format
					if (strSplit.length>2){
						value = "Error:unrecognizable input: "
							+"put a copy of the input's"
							+" packet payload here";
					}
					else{
						if (Map.containsKey(strSplit[1])){
							Map.remove(strSplit[1]);
							value = "Ok";
						}
						else
							value = "no match";
					}
					break;
				}
				
				// Error info
				default :{
					value = "Error:unrecognizable input: "
							+"put a copy of the input's"
							+" packet payload here";
					break;
				}
		
			}

			// trans string to byte
			byte[] buffer = value.getBytes();

			// create outcoming packet
			DatagramPacket outpkt = new DatagramPacket(buffer,
										buffer.length,
										clientAdr);
			sock.send(outpkt);
			// and send it back
			
		}
	}
}

