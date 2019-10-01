import java.io.*;
import java.net.*;

/** TCP Map Client 
 *  author: Haiyu Wang and Haodong Huang
 * 
 *  usage: java TcpMapCient serverName [port number]
 * 
 *  Open a connection to the server and print the response.
 *  When typing a blank line, connection closes and program exits.
 *  Port number are optional. Default port number is 30123. 
 * 
 *  Use Jon Turner's program - TCP echo client (7/2012) as a starting point
 */

public class TcpMapClient {
	public static void main(String args[]) throws Exception {
		// connect to remote server
		int port = 30123;
		if (args.length > 1) port = Integer.parseInt(args[1]);
		Socket sock = new Socket(args[0], port);
		
		// create reader & writer for socket's I/O
		BufferedReader  in = new BufferedReader(new InputStreamReader(
				    	 sock.getInputStream(),"US-ASCII"));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				    	 sock.getOutputStream(),"US-ASCII"));

		// create reader for System.in
		BufferedReader sysin = new BufferedReader(new InputStreamReader(
					   System.in));

		String line;

		while (true) {
			// reminder
			System.out.print("Please type a string: ");

			// if it is a bank line, break and close connection
			line = sysin.readLine();
			if (line == null || line.length() == 0) break;
			
			// write line on socket and print reply to System.out
			out.write(line); out.newLine(); out.flush();
			System.out.println(in.readLine());
		}
		sock.close();
	}
}
