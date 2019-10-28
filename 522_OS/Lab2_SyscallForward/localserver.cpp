#include "local.h"

#define ADDR "local"

using namespace std;

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

int main (int argc, char *argv[]) {
	if (DATA_BUF_NUM*DATA_BUF_SIZE>127*1024*1024) {
		cout << "The size of redzone exceed the maximum" << endl;
		cout << "Please adjust DATA_BUF_NUM and DATA_BUF_SIZE to get a partical redzone." << endl;
		exit(EXIT_FAILURE); 
	}
	int sockfd,clientfd;
	struct sockaddr_un serverAdr, clientAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	int bufsize;
	extern int errno;

	// create a socket for connection.
	sockfd = socket(AF_LOCAL, SOCK_STREAM, 0);
	if (sockfd == -1)
		handle_error("socket");

	// clear the server struct
	memset(&serverAdr, 0, sizeof(struct sockaddr_un));

	serverAdr.sun_family = AF_LOCAL;

	// Assign a path to server 
	strncpy(serverAdr.sun_path, ADDR, 
			sizeof(serverAdr.sun_path)-1);

	// bind socket with server address
	if (bind(sockfd,(struct sockaddr *) &serverAdr, sizeof(serverAdr)) == -1)
		handle_error("bind");

	// listen for connection, time limit is 20
	if (listen(sockfd, 20)== -1)
		handle_error("listen");

	clientAdr_size = sizeof(clientAdr);

	int redzone = shm_open("redzone",O_RDWR|O_CREAT|O_NONBLOCK, 0777);
	if (redzone<0)
		handle_error("shm_open");
	if (ftruncate(redzone,sizeof(struct redzone))<0)
		handle_error("ftruncate");
	struct redzone *redzone_start;
	redzone_start = (struct redzone *) mmap(NULL, sizeof(struct redzone), PROT_READ | PROT_WRITE, MAP_SHARED, redzone, 0);

	// if (redzone_start<0)
	// 	handle_error("mmap");


	clientfd = accept(sockfd, (struct sockaddr *) &clientAdr, &clientAdr_size);


	if (clientfd == -1)	{
		handle_error("accept");
	} else {
		printf("New Connection Established\n");
	}

	struct redzone *s = (struct redzone *)redzone_start;

	// int i=0;
	// int j=0;
	// for (i=0;i<BUFSIZE-1;i++) {
	// 	cout << "i:";
	// 	sprintf(s->str_mat[i], "%d", i);
	// 	printf("%d %d %s\n",i, s->str_mat[i], s->str_mat[i]);
	// };

	// use a loop to accept multiple client
	while (1) {
		
		char msg[BUFSIZE];

		if (recv(clientfd, msg, BUFSIZE,0)<0)
			handle_error("recv");
		// cout << "Signal Reveived: " << msg << endl;
		
		if (strcmp(msg,"ipc_close")==0) {
			struct redzone *s = (struct redzone *)redzone_start;
			int close_ret_value = close(s->fd);
			if (close_ret_value<0)
				handle_error("close");
			char ret[BUFSIZE];
			sprintf(ret, "%d", close_ret_value);
			send(clientfd, ret, sizeof(ret),0);
		} else if (strcmp(msg,"ipc_open")==0) {
			struct redzone *s = (struct redzone *)redzone_start;
			int fd = open(s->filename,s->flags,s->mode);
			if (fd<0)
				handle_error("open");
			char ret[BUFSIZE];
			sprintf(ret, "%d", fd);
			if (send(clientfd, ret, sizeof(ret),0)<0)
				handle_error("send");
		} else if (strcmp(msg,"ipc_read")==0)	{
			// int read_fd = shm_open("read", O_RDWR, S_IRWXU);
			struct redzone *s = (struct redzone *)redzone_start;
			// cout << "s->fd:" << s->fd << endl;
			ssize_t count=read(s->fd,s->str_mat[s->index],s->read_size);
			if (count<0)
				handle_error("read");
			char ret[BUFSIZE];
			sprintf(ret, "%d", count);
			if (send(clientfd, ret, sizeof(ret),0)<0)
				handle_error("send");
		} else if (strcmp(msg,"ipc_write")==0) {
			struct redzone *s = (struct redzone *)redzone_start;
			ssize_t count=write(s->fd,s->str_mat[s->index],s->read_size);
			if (count<0)
				handle_error("write");
			char ret[BUFSIZE];
			sprintf(ret, "%d", count);
			if (send(clientfd, ret, sizeof(ret),0)<0)
				handle_error("send");
			// ssize_t count = write(s->fd,s->buf,s->read_size);
		} else if (strcmp(msg,"ipc_terminate")==0) {
			break;
		}
	}

	if (close(sockfd)<0)
		handle_error("close");
	if (unlink(ADDR)<0)
		handle_error("unlink");

	return 0;
}
