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

/**
*
* Haiyu Wang	
*
* Usage: ./netclient
*
* create net socket and request for connection to server
* write strings to socket
* use man 2 bind example code as a starting point. 
*
*/

int main (int argc, char *argv[])
{
	int sockfd;
	struct sockaddr_in serverAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	char *str[10]={"123","456","789"};
	int bufsize;
	int i;
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

	// the ip address of "shell.cec.wustl.edu" is 128.252.167.161
	serverAdr.sin_addr.s_addr = inet_addr("127.0.0.1"); 

	// set port 2048
	serverAdr.sin_port = htons(2048);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	while(1)
	{
		memset(buf, 0, sizeof(buf));
		for (i=0;i<3;i++)
		{
			write(sockfd,str[i],10);
		}
		bufsize=strlen(buf);
		buf[bufsize]='\0';
		if (strcmp(buf,"quit")==0)
			break;
	}

	close(sockfd);
	return 0;

}