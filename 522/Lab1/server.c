#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <assert.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h> 
#include <sys/ioctl.h> 


#define IPADR "127.0.0.1"
#define TIMEOUT 10
#define BUF_LEN 128

void err(char *str);
int get_count(char *buf, char *result);
int *open_file(char *buf,int count);

int main(int argc, char *args[])
{
	int fd,outfd;
	int count = 0;
	FILE *fp;
	char *result = NULL;
	char buf[BUF_LEN],copy[BUF_LEN];
	
	if (argc!=3)
		err("Arguments");
	fd  = open(args[1],O_RDONLY);
	fp = fdopen(fd,"r");
	if (fp==NULL)
		err("open");
	memset(buf,0,BUF_LEN);
	memset(copy,0,BUF_LEN);
	char readstr[BUF_LEN];
	char *fliename[BUF_LEN];
	while(!feof(fp))
	{
		fgets(readstr,BUF_LEN,fp);
		int str_len = strlen(readstr);
		fliename[count] = (char *)malloc(str_len*sizeof(char));
		strcpy(fliename[count],readstr);
		printf("%s", fliename[count]);
		count++;
	}
	close(fd);
	outfd = open(fliename[0],O_WRONLY);
	if (outfd<0)
		err("open");
	printf("%d\n", count);
	read(fd,buf,sizeof(buf));

	strcpy(copy,buf);
	result = strtok(copy,"\n");
	outfd = open(result,O_WRONLY);
	if (outfd<0)
		err("open");
	result = strtok(NULL,"\n");
	count = get_count(buf,result);
	int *infd;
	infd = (int *) malloc(count*sizeof(int));
	infd = open_file(buf,count);
	
	int serverfd, clientfd;
	struct sockaddr_in serverAdr, clientAdr;
	socklen_t clientAdr_size;
	int port_number;
	port_number = atoi(args[2]);
	clientAdr_size = sizeof(clientAdr);

	serverfd = socket(AF_INET, SOCK_STREAM, 0);
	if (serverfd == -1)
		err("server socket");

	memset(&serverAdr, 0, sizeof(serverAdr));
	serverAdr.sin_family = AF_INET;
	serverAdr.sin_addr.s_addr = INADDR_ANY;
	serverAdr.sin_port = htons(port_number);

	if (bind(serverfd, (struct sockaddr *) &serverAdr,sizeof(serverAdr)) == -1)
		err("bind");

	if (listen(serverfd,20) == -1)
		err("listen");
	int res;
	int out;
	fd_set readfds, testfds,writefds, tmpfds;
	FD_ZERO(&readfds);
	FD_ZERO(&writefds);
	FD_SET(serverfd,&readfds);
	FD_SET(serverfd,&writefds);
	struct timeval tv;
	tv.tv_sec = TIMEOUT;
 	tv.tv_usec = 0;

	while(1)
	{
		char ch;
		char readbuf[800];
		int fd1, nread;
		int i=0;
		FD_ZERO(&readfds);
		FD_SET(serverfd,&readfds);
		printf("server waiting\n"); 
		char rdbuf[800];

		res = select(FD_SETSIZE, &readfds, NULL, NULL, &tv);
		if (res<1)
			err("select res");

		for (fd1 = 0; fd1<FD_SETSIZE; fd1++)
		{
			if (FD_ISSET(fd1,&readfds))
			{
				if (fd1 == serverfd)
				{
					
					clientfd = accept(serverfd,(struct sockaddr *) &clientAdr,&clientAdr_size);
					//FD_SET(clientfd,&readfds);
					FD_SET(clientfd,&readfds);
					printf("adding client on fd %d\n", clientfd);
				}
				else
				{
					read(fd1,&ch,1);
					read(infd[i],&readbuf,sizeof(readbuf));
					i++;
                    write(fd1, &readbuf, sizeof(readbuf));
                    write(fd1,&ch,1);
	                read(fd1,&rdbuf,sizeof(readbuf));
	                printf("%s\n", rdbuf);
				}
				
				memset(readbuf,0, sizeof(readbuf));
			}
			
		}
	}


	close(outfd);

}

void err(char *str)
{
	perror(str);
	exit(EXIT_FAILURE);
}

int get_count(char *buf,char *result)
{
	char copy[1000];
	strcpy(copy,buf);
	int count = 0;
	while (result!=NULL)
	{
		count++;
		result = strtok(NULL,"\n");
	}
	return count;
}

int *open_file(char *buf, int count)
{
	int *infd;;
	int i = 0;
	char *result = NULL;
	char *str[20];
	infd = (int *) malloc(count*sizeof(int));
	result = strtok(buf,"\n");
	result = strtok(NULL,"\n");
	while (result!=NULL)
	{
		infd[i] = open(result,O_RDONLY);
		if (infd[i]<0)
			err("open");
		i++;
		result = strtok(NULL,"\n");
	}
	return infd;
}