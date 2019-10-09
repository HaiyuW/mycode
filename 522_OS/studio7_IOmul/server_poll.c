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
#include <time.h>
#include <poll.h>

#define IPADR "127.0.0.1"
#define PORT 2048
#define TIMEOUT 10
#define BUF_LEN 512

void err(char *str)
{
	perror(str);
	exit(EXIT_FAILURE);
}

int main (int argc, char *args[])
{
	int serverfd, clientfd;
	struct sockaddr_in serverAdr, clientAdr;
	socklen_t clientAdr_size;
	clientAdr_size = sizeof(clientAdr);
	
	serverfd = socket(AF_INET, SOCK_STREAM, 0);
	if (serverfd == -1)
		err("server socket");

	memset(&serverAdr, 0, sizeof(serverAdr));
	serverAdr.sin_family = AF_INET;
	serverAdr.sin_addr.s_addr = INADDR_ANY;
	serverAdr.sin_port = htons(PORT);

	if (bind(serverfd, (struct sockaddr *) &serverAdr,sizeof(serverAdr)) == -1)
		err("bind");

	if (listen(serverfd,20) == -1)
		err("listen");

	struct pollfd *client;
	client = (struct pollfd*)malloc(64*sizeof(struct pollfd));
	int i;
	for (i=0;i<64;i++)
		client[i].fd = -1;
	// add STDIN_FILENO to the set
	client[0].fd = STDIN_FILENO;
	client[0].events = POLLIN;
	// add serverfd to the set
	client[1].fd = serverfd;
	client[1].events = POLLIN;
	int ret;

	int n_links = 1;

 	while(1)
 	{
 		ret = poll(client, n_links+1, -1); 
 		if (ret == -1)
 			err("poll");
 		if (ret == 0)
 			continue;

 		// handle STDIN_FILENO
 		if (client[0].revents&POLLIN)
 		{
 			char buf[BUF_LEN+1];
 			int len;
 			len = read(STDIN_FILENO,buf,BUF_LEN);
 			if (len == -1)
 				err("read");
 			if (len == 0)
 			{
 				printf("remove STDIN\n");
 				client[0].fd = -1;
 				continue;
 			}
 			buf[len-1]='\0';
 			if (strcmp(buf,"quit")==0)
 			{
 				break;
 			}

 			printf("read from STDIN: %s\n", buf);
 		} 		

 		// handle serverfd and add clientfd to the set
 		if (client[1].revents&POLLIN)
 		{
 			clientfd = accept(serverfd,(struct sockaddr *) &clientAdr,&clientAdr_size);
 			printf("add client fd is %d\n", clientfd);
 			if (clientfd == -1)
 				err("accept");

 			for (i=2;i<63;i++)
 			{
 				if (client[i].fd<0)
 				{
 					client[i].fd = clientfd;
 					client[i].events = POLLIN;
 					break;
 				}
 				
 			}

 			if (i==63)
			{
				printf("too many clients\n");
				exit(EXIT_SUCCESS);
			}
			if (i>n_links)
				n_links=i;
			if (--ret<=0)
				continue;
 		}

 		// handle clientfd
 		for (i=2;i<n_links+1;i++)
 		{
 			clientfd = client[i].fd;
 			if (clientfd<0)
 				continue;
 			if (client[i].revents&POLLIN)
 			{
 				char buf[BUF_LEN+1];
 				int len;
 				len = read(clientfd,buf,BUF_LEN);

 				if (len>0)
 				{
 					write(clientfd,buf,len);
 					printf("read from client %d: %s", clientfd, buf);
 				}
 				if (len == 0)
 				{
 					client[i].fd = -1;
 					continue;
 				}
 			}
 		
 		}
	}

	// close clientfd
	for (i=2;i<n_links+1;i++)
	{
		clientfd = client[i].fd;
		if (clientfd>0)
			close(clientfd);
	}
	close(serverfd);

}
