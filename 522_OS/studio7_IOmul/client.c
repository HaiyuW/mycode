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
* Usage: ./client
*
* create net socket and request for connection to server
* write strings to socket and close
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

	serverAdr.sin_addr.s_addr = inet_addr("127.0.0.1"); 

	// set port 2048
	serverAdr.sin_port = htons(2048);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	len = read(sockfd,buf,BUF_LEN);
 	if (len == -1)
 		err("read");
 	if (len == 0)
 		return 0;
 	buf[len]='\0';
 	printf("%s\n", buf);

	close(sockfd);
	return 0;

}