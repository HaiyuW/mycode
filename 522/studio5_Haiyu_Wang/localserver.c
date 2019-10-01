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
* Usage: ./localserver
*
* create local socket named local and listen for connection
* accept clients and print what clients write.
* use man 2 bind example code as a starting point. 
*
*/

int main (int argc, char *argv[])
{
	int sockfd,clientfd;
	struct sockaddr_un serverAdr, clientAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	int bufsize;
	extern int errno;

	// create a socket for connection.
	sockfd = socket(AF_LOCAL, SOCK_STREAM, 0);
	if (sockfd == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// clear the server struct
	memset(&serverAdr, 0, sizeof(struct sockaddr_un));

	serverAdr.sun_family = AF_LOCAL;

	// Assign a path to server 
	strncpy(serverAdr.sun_path, "/home/pi/studio5/local", 
			sizeof(serverAdr.sun_path)-1);

	// bind socket with server address
	if (bind(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// listen for connection, time limit is 20
	if (listen(sockfd, 20)== -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	clientAdr_size = sizeof(clientAdr);

	// use a loop to accept multiple client
    while(1)
    {

		clientfd = accept(sockfd, (struct sockaddr *) &clientAdr,
							&clientAdr_size);


		if (clientfd == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}
		else
		{
			printf("New Connection Established\n");
			while(1)
			{
				memset(buf,0,sizeof(buf));
				if (read(clientfd, &buf, 1000)==0)
					break;
				bufsize=strlen(buf);
				buf[bufsize-1]='\0';

				// if the buf is 'quit', close the socket.
				if (strcmp(buf,"quit")==0)
				{
					close(clientfd);
					close(sockfd);
					unlink("/home/pi/studio5/local");
					exit(EXIT_SUCCESS);

				}
				else
				{
					buf[bufsize-1]='\n';
					printf("%s",buf);
				}
			}
		}
			
	}
	close(clientfd);

	close(sockfd);
	unlink("/home/pi/studio5/local");

	return 0;
}
