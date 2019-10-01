#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>

/**
*
* Haiyu Wang	
*
* Usage: ./localclient
*
* request for connection to server through socket.
* write strings and send them to the server
* when sending 'quit', the server can be closed.
* use man 2 bind example code as a starting point. 
*
*/

int main (int argc, char *argv[])
{
	int sockfd;
	struct sockaddr_un serverAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	int bufsize;
	extern int errno;

	// open socket
	sockfd = socket(AF_LOCAL, SOCK_STREAM, 0);
	if (sockfd == -1)
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// clear serverAdr and set serverAdr
	memset(&serverAdr, 0, sizeof(struct sockaddr_un));

	serverAdr.sun_family = AF_LOCAL;

	strncpy(serverAdr.sun_path, "/home/pi/studio5/local", 
			sizeof(serverAdr.sun_path)-1);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1)
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	while(1)
	{
		// clear buf
		memset(buf, 0, sizeof(buf));
		if (fgets(buf, 1000, stdin)==NULL)
			break;
		write(sockfd, buf, strlen(buf)); // write buf to socket
		bufsize=strlen(buf);

		// if buf=='quit', then close client
		if (strcmp(buf,"quit")==0)
		{
			close(sockfd);
			exit(EXIT_SUCCESS);
		}
	}

	close(sockfd);
	
	return 0;

}