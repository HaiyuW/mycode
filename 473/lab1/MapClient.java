import java.io.*;
import java.net.*;

/**
 * author: Haiyu Wang
 * usage: java MapClient serverName port string
 * test: TestScript
 *
 * Send a packet to the named server:port containing the given string.
 * Wait for reply packet and print its contents.
**/

public class MapClient {
	public static void main(String args[]) throws Exception {
		// get server address and port
		InetAddress serverAdr = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		// create socket
		DatagramSocket sock = new DatagramSocket();

		// encode byte array with US_ASCII
		// create outpkt and send
		byte[] outbuf = args[2].getBytes("US-ASCII");
		DatagramPacket outpkt = new DatagramPacket(outbuf,
									outbuf.length,
							   		serverAdr,
							   		port);
		sock.send(outpkt);	// send packet to server

		// create buffer and packet for reply
		byte[] inbuf = new byte[1000];
		DatagramPacket inpkt = new DatagramPacket(inbuf,
										inbuf.length);
		sock.receive(inpkt);	// wait for reply

		// get content of buf and trans to String and print
		String reply = new String(inbuf,
								0,
								inpkt.getLength(),
								"US-ASCII");
		System.out.println(reply);
		sock.close();
	}
}
