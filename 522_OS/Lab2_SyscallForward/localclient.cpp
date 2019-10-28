#include "local.h"

#define ADDR "local"

using namespace std;

const char *closename = "close";

int ipc_open(char *pathname, int flags, mode_t mode);
int ipc_close(int fd);
ssize_t ipc_read(int fd, void * buf, size_t count);
ssize_t ipc_write(int fd, void * buf, size_t count);
int socket_create(void);

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

int sockfd;

int main (int argc, char *argv[]) {
	if (DATA_BUF_NUM*DATA_BUF_SIZE>127*1024*1024) {
		cout << "The size of redzone exceed the maximum" << endl;
		cout << "Please adjust DATA_BUF_NUM and DATA_BUF_SIZE to get a partical redzone." << endl;
		exit(EXIT_FAILURE); 
	}
	sockfd=socket_create();

	int redzone = shm_open("redzone",O_RDWR, 0777);
	if (redzone<0)
		handle_error("shm_open");
	struct redzone *redzone_start;
	redzone_start = (struct redzone *) mmap(NULL, sizeof(struct redzone), PROT_READ | PROT_WRITE, MAP_SHARED, redzone, 0);
	// if (redzone_start<0)
		// handle_error("mmap");
	memset(redzone_start,0,sizeof(struct redzone));

	struct redzone * s = (struct redzone *)redzone_start;
	cout << sizeof(struct redzone) << endl;
	strncpy(s->filename, "test.txt", 64);
	s->flags = O_RDWR|O_APPEND;
	s->mode = 0;
	int fd = ipc_open(s->filename, s->flags,s->mode);
	s->fd=fd;
	s->read_size=64;
	s->index = 0;
	s->buf=s->str_mat[s->index];
	// printf("%d %d\n", s->buf, s->str_mat[s->index]);
	ipc_read(fd,(void*)s->buf,s->read_size);
	ipc_write(fd,(void*)s->buf,s->read_size);
	ipc_close(fd);


	// char buf[BUFSIZE];
	// read(fd,buf,BUFSIZE);
	// cout<<buf<<endl;

	char msg[BUFSIZE];
	strcpy(msg,"ipc_terminate");
	if (send(sockfd,msg,sizeof(msg),0)<0)
		handle_error("send");
	if (close(sockfd)<0)
		handle_error("close");
	return 0;

}

int ipc_open(char *pathname, int flags, mode_t mode) {
	char msg[BUFSIZE];
	strcpy(msg,"ipc_open");
	if (send(sockfd,msg,sizeof(msg),0)<0)
		handle_error("send");
	char ret[BUFSIZE];
	if (recv(sockfd,ret,BUFSIZE,0)<0)
		handle_error("recv");
	return atoi(ret);
}

int ipc_close(int fd) { 
	char msg[BUFSIZE];
	strcpy(msg,"ipc_close");
	if (send(sockfd,msg,sizeof(msg),0)<0)
		handle_error("send");
	char ret[BUFSIZE];
	if (recv(sockfd,ret,BUFSIZE,0)<0)
		handle_error("recv");
	return atoi(ret);
}

ssize_t ipc_read(int fd, void * buf, size_t count) {
	if (count>DATA_BUF_SIZE)
		return -1;
	char msg[BUFSIZE];
	strcpy(msg,"ipc_read");
	if (send(sockfd,msg,sizeof(msg),0)<0)
		handle_error("send");
	char ret[BUFSIZE];
	if (recv(sockfd,ret,BUFSIZE,0)<0)
		handle_error("recv");
	//cout << (char*)buf << endl;
	cout << atoi(ret) << endl;
	return (ssize_t)atoi(ret);
}
ssize_t ipc_write(int fd, void * buf, size_t count) {
	if (count>DATA_BUF_SIZE)
		return -1;
	char msg[BUFSIZE];
	strcpy(msg,"ipc_write");
	if (send(sockfd,msg,sizeof(msg),0)<0)
		handle_error("send");
	char ret[BUFSIZE];
	if (recv(sockfd,ret,BUFSIZE,0)<0)
		handle_error("recv");
	//cout << "ipc_write ret value:" << atoi(ret) << endl;
	return (ssize_t)atoi(ret);
}

int socket_create(void) {
	int sockfd;
	struct sockaddr_un serverAdr;
	socklen_t clientAdr_size;
	char buf[1000];
	int bufsize;
	extern int errno;

	// open socket
	sockfd = socket(AF_LOCAL, SOCK_STREAM, 0);
	if (sockfd == -1)
		handle_error("socket");

	// clear serverAdr and set serverAdr
	memset(&serverAdr, 0, sizeof(struct sockaddr_un));

	serverAdr.sun_family = AF_LOCAL;

	strncpy(serverAdr.sun_path, ADDR, 
			sizeof(serverAdr.sun_path)-1);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1)
		handle_error("connect");
	
	return sockfd;
}
