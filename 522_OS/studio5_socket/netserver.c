#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <netinet/ip.h>
#include <arpa/inet.h>

/**
*
* Haiyu Wang	
*
* Usage: ./netserver
*
* create net socket and listen for connection
* accept clients and print what clients write.
* use man 2 bind example code as a starting point. 
*
*/

int main (int argc, char *argv[])
{
	int sockfd,clientfd;
	struct sockaddr_in serverAdr, clientAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	int bufsize;
	extern int errno;

	// create socket
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// clear serverAdr and set it
	memset(&serverAdr, 0, sizeof(serverAdr));

	serverAdr.sin_family = AF_INET;

	// INADDR_ANY means any address for binding
	serverAdr.sin_addr.s_addr = INADDR_ANY;  

	// port number is 2048
	serverAdr.sin_port = htons(2048);

	// bind the serverAdr
	if (bind(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// listen for connection
	if (listen(sockfd, 20)== -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	clientAdr_size = sizeof(clientAdr);

    while(1){

    	// accept client
		clientfd = accept(sockfd, (struct sockaddr *) &clientAdr,
							&clientAdr_size);


		if (clientfd == -1){
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}
		else
		{
			printf("New Connection Established\n");
			while(1){

				// clear buf
				memset(buf,0,sizeof(buf));
				if (read(clientfd, &buf, 10)==0)
					break;
				bufsize=strlen(buf);
				buf[bufsize-1]='\0';

				// if buf == quit, close socket
				if (strcmp(buf,"quit")==0)
				{
					close(clientfd);
					close(sockfd);

					exit(EXIT_SUCCESS);

				}
				else{
					// else print whatever been read from the socket
					buf[bufsize-1]='\n';
					printf("%s",buf);
				}
			}
		}
			
	}
	close(clientfd);

	close(sockfd);

	return 0;
}