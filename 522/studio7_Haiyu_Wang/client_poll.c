#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include <unistd.h>

#define BUF_LEN 512
/**
*
* Haiyu Wang	
*
* Usage: ./client_poll
*
* create net socket and request for connection to server
* repeatedly write strings to socket 
*
*/

void err(char *str)
{
	perror(str);
	exit(EXIT_FAILURE);
}

int main (int argc, char *argv[])
{
	int sockfd;
	struct sockaddr_in serverAdr;
	socklen_t clientAdr_size;
	char buf[BUF_LEN];
	int bufsize;
	int len;
	extern int errno;

	// create a socket
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// clear serverAdr and set it
	memset(&serverAdr, 0, sizeof(serverAdr));

	serverAdr.sin_family = AF_INET;

	// the ip address is 127.0.0.1
	serverAdr.sin_addr.s_addr = inet_addr("127.0.0.1"); 

	// set port 2048
	serverAdr.sin_port = htons(2048);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// write the message from STDIN to the socket
	while(1)
	{
		memset(buf,0,BUF_LEN);
		int len;
 		len = read(STDIN_FILENO,buf,BUF_LEN);
		if (len == -1)
			err("read");
		if (len == 0)
		{
			write(sockfd,buf,len);
			break;
		}
 		buf[len] = '\0';
 		write(sockfd,buf,len);
	}
	

	close(sockfd);
	return 0;

}