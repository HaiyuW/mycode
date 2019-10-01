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

#define IPADR "127.0.0.1"
#define PORT 2048
#define TIMEOUT 10
#define BUF_LEN 512

void err(char *str)
{
	perror(str);
	exit(EXIT_FAILURE);
}

// produce message
char *msg();

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

	int ret;
	int fd;
	fd_set readfds;
	FD_ZERO(&readfds);
	FD_SET(serverfd,&readfds);
	FD_SET(STDIN_FILENO, &readfds);
	struct timeval tv;
	tv.tv_sec = TIMEOUT;
 	tv.tv_usec = 0;

 	while(1)
 	{
 		FD_ZERO(&readfds);
		FD_SET(serverfd,&readfds);
		FD_SET(STDIN_FILENO, &readfds);
 		ret = select (FD_SETSIZE, &readfds, NULL, NULL, NULL); 

 		if (ret == -1)
 			err("select");

 		for (fd=0;fd<FD_SETSIZE;fd++)
 		{
 			if (FD_ISSET(fd,&readfds))
 			{
 				// handle STDIN_FILENO
 				if(fd == STDIN_FILENO)
 				{
 					char buf[BUF_LEN];
 					int len;
 					len = read(fd,buf,BUF_LEN);
 					if (len == -1)
 						err("STDIN read");
 					if (len == 0)
 					{
 						return 0;
 					}
 					buf[len]='\0';
 					printf("read: %s", buf);

 				}
 				// handle serverfd
 				else if (fd == serverfd)
 				{
 					clientfd = accept(serverfd,(struct sockaddr *) &clientAdr,&clientAdr_size);
 					if (clientfd == -1)
 						err("accept");
 					printf("adding client on fd %d\n", clientfd);
 					char *buf;
					buf = msg();
 					write(clientfd,buf,BUF_LEN);
 					free(buf);
 				}
 				else
 				{

 				}
 			}
 		}
 	}
}

char *msg()
{
	char *buf;
 	buf = (char *)malloc(BUF_LEN*sizeof(char));
	char *msg = "Server's hostname is ";
 	char hostname[32];
 	gethostname(hostname,32);
 	char *time1 = ", current time is ";
 	time_t ptr;
 	time(&ptr);
 	struct tm *p = NULL;
 	p = gmtime(&ptr);
 	char hour[10];
 	char min[10];
 	char sec[10];
 	sprintf(hour,"%d",p->tm_hour);
 	sprintf(min,"%d",p->tm_min);
 	sprintf(sec,"%d",p->tm_sec);
 	strcat(buf,msg);
 	strcat(buf,hostname);
 	strcat(buf,time1);
 	strcat(buf,hour);
 	strcat(buf,":");
 	strcat(buf,min);
 	strcat(buf,":");
 	strcat(buf,sec);
 	return buf;

}