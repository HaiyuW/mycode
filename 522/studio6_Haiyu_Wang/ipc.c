#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/un.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>
#include <sys/wait.h>
#include <stdatomic.h>
#include <netinet/ip.h>
#include <arpa/inet.h>

#define PIPE_BUF 16
_Atomic int state = ATOMIC_VAR_INIT(0);
_Atomic int num = ATOMIC_VAR_INIT(0);
unsigned int times;
char ipc_type[100];

// parent handler for SIGUSR1
void phandler1(int arg)
{
	state = 1;
}

// parent handler for SIGUSR2
void phandler2(int arg)
{
	state = 2;

}

// child handler for SIGUSR2
void chandler(int arg)
{
	atomic_fetch_add(&num,1);
}

int main (int argc, char *argv[])
{
	extern int errno;

	if (argc != 3)
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	times = atoi(argv[1]);
	strcpy(ipc_type,argv[2]);

	
	struct timespec start = {0,0};	
	struct timespec end = {0,0};

	int sec = 0;
	long nansec = 0;

	int count=0;
	pid_t ppid,cpid;
	struct sigaction psig1,psig2;
	psig1.sa_handler = phandler1;
	psig1.sa_flags = SA_RESTART;

	psig2.sa_handler = phandler2;
	psig2.sa_flags = SA_RESTART;

	struct sigaction csig;
	csig.sa_handler = chandler;
	csig.sa_flags = SA_RESTART;

	typedef union ipc_data{
			char full_data[PIPE_BUF];
			struct 
			{
				int index;
				char data[PIPE_BUF-sizeof(int)];
			}st_data;
		}ipc_data_t;

	ipc_data_t ipc_p, ipc_c;
	memset(ipc_p.full_data,0,sizeof(PIPE_BUF));

	ipc_p.st_data.index = 0;
	ipc_c.st_data.index = 0;

	// signals
	if (strcmp(ipc_type,"signals") == 0)
	{
		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);
		
		ppid = getpid();
		cpid = fork();

		if (cpid == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}
		else if (cpid > 0)
		{
			printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
			while(state!=1);
			clock_gettime(CLOCK_MONOTONIC_RAW,&start);
			while(state!=2)
			{
				kill(cpid,SIGUSR2);
				count++;
			}
			clock_gettime(CLOCK_MONOTONIC_RAW,&end);	
			printf("SIGUSR2 number is %d\n", count);
			sec = end.tv_sec - start.tv_sec;
			nansec = end.tv_nsec - start.tv_nsec;
			printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
		}

		else{
			printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);
			sigaction(SIGUSR2, &csig, NULL);
			if (kill(ppid,SIGUSR1) == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			while(num!=times);
			kill(ppid,SIGUSR2);
		}

		return 0;
	}

	// pipe
	else if (strcmp(ipc_type,"pipe") == 0)
	{

		int pipefd[2];
		ppid = getpid();


		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);
		if (pipe(pipefd) == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		cpid = fork();
		if (cpid == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		if (cpid!=0)
		{

			printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
			while(state!=1) ;
			clock_gettime(CLOCK_MONOTONIC_RAW,&start);
			close(pipefd[0]);
			while (ipc_p.st_data.index<times)
			{
				// encode the index
				memcpy(ipc_p.full_data,&ipc_p.st_data.index,sizeof(int));
				write(pipefd[1],ipc_p.full_data,PIPE_BUF);
				ipc_p.st_data.index++;
			}
			close(pipefd[1]);
			while(state!=2);
			clock_gettime(CLOCK_MONOTONIC_RAW,&end);
			sec = end.tv_sec - start.tv_sec;
			nansec = end.tv_nsec - start.tv_nsec;
			printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
			_exit(EXIT_SUCCESS);
		}
		else
		{	printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);
			kill(ppid,SIGUSR1);
			close(pipefd[1]);
			while (ipc_c.st_data.index<times-1)
			{
				read(pipefd[0],ipc_c.full_data,PIPE_BUF);
				memcpy(&ipc_c.st_data.index, ipc_c.full_data,sizeof(int));
			}
			kill(ppid,SIGUSR2);
			close(pipefd[0]);
			exit(EXIT_SUCCESS);
		}

	}

	// FIFO
	else if (strcmp(ipc_type,"FIFO") == 0)
	{
		ppid = getpid();


		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);

		int fifofd;

		fifofd = mkfifo("myfifo",0644);
	    if (fifofd == -1)
	    {	
	    	printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

	    printf("Create FIFO Success\n");

		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);
		
		ppid = getpid();
		cpid = fork();

		if (cpid == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		if (cpid!=0)
		{
			printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
			while(state!=1) ;
			clock_gettime(CLOCK_MONOTONIC_RAW,&start);
			fifofd = open("myfifo",O_WRONLY);
			if (fifofd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			while (ipc_p.st_data.index<times)
			{
				memcpy(ipc_p.full_data,&ipc_p.st_data.index,sizeof(int));
				write(fifofd,ipc_p.full_data,PIPE_BUF);
				ipc_p.st_data.index++;
			}
			close(fifofd);
			while(state!=2);
			clock_gettime(CLOCK_MONOTONIC_RAW,&end);
			sec = end.tv_sec - start.tv_sec;
			nansec = end.tv_nsec - start.tv_nsec;
			printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
			_exit(EXIT_SUCCESS);
		}
		else
		{
			printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);
			kill(ppid,SIGUSR1);
			fifofd = open("myfifo",O_RDONLY);
			while (ipc_c.st_data.index<times-1)
			{
				read(fifofd,ipc_c.full_data,PIPE_BUF);
				memcpy(&ipc_c.st_data.index, ipc_c.full_data,sizeof(int));
			}
			close(fifofd);
			kill(ppid,SIGUSR2);
			exit(EXIT_SUCCESS);
		}
	}

	// local socket
	else if (strcmp(ipc_type,"lsock") == 0)
	{
		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);
		
		ppid = getpid();
		cpid = fork();

		if (cpid == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		if (cpid != 0)
		{
			printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
			int serverfd, clientfd;
			struct sockaddr_un serverAdr, clientAdr;
			socklen_t clientAdr_size;

			serverfd = socket(AF_LOCAL, SOCK_STREAM, 0);
			if (serverfd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			memset(&serverAdr, 0, sizeof(struct sockaddr_un));
			serverAdr.sun_family = AF_LOCAL;
			strncpy(serverAdr.sun_path, "/Users/a1100/Desktop/studio6/local", 
				sizeof(serverAdr.sun_path)-1);

			if (bind(serverfd,(struct sockaddr *) &serverAdr,
				sizeof(serverAdr)) == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			if (listen(serverfd, 20)== -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			clientAdr_size = sizeof(clientAdr);

			while(state!=1);

			clock_gettime(CLOCK_MONOTONIC_RAW,&start);

			clientfd = accept(serverfd, (struct sockaddr *) &clientAdr,
							&clientAdr_size);
			if (clientfd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			while (ipc_p.st_data.index<times)
			{
				memcpy(ipc_p.full_data,&ipc_p.st_data.index,sizeof(int));
				write(clientfd,ipc_p.full_data,PIPE_BUF);
				ipc_p.st_data.index++;
			}
			while(state!=2) ;
			clock_gettime(CLOCK_MONOTONIC_RAW,&end);
			sec = end.tv_sec - start.tv_sec;
			nansec = end.tv_nsec - start.tv_nsec;
			printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
			close(clientfd);
			close(serverfd);
			unlink("/Users/a1100/Desktop/studio6/local");
			exit(EXIT_SUCCESS);
		}
		else
		{
			printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);
			int sockfd;
			struct sockaddr_un serverAdr;
			socklen_t clientAdr_size;

			sockfd = socket(AF_LOCAL, SOCK_STREAM, 0);
			if (sockfd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			memset(&serverAdr, 0, sizeof(struct sockaddr_un));

			serverAdr.sun_family = AF_LOCAL;

			strncpy(serverAdr.sun_path, "/Users/a1100/Desktop/studio6/local", 
					sizeof(serverAdr.sun_path)-1);

			if (connect(sockfd,(struct sockaddr *) &serverAdr,
					sizeof(serverAdr)) == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			kill(ppid,SIGUSR1);
			while (ipc_c.st_data.index<times-1)
			{
				read(sockfd,ipc_c.full_data,PIPE_BUF);
				memcpy(&ipc_c.st_data.index, ipc_c.full_data,sizeof(int));
			}
			kill(ppid,SIGUSR2);
			close(sockfd);
			exit(EXIT_SUCCESS);
		}
		
	}

	// net socket
	else if (strcmp(ipc_type,"socket") == 0)
	{
		sigaction(SIGUSR1, &psig1, NULL);
		sigaction(SIGUSR2, &psig2, NULL);
		
		ppid = getpid();
		cpid = fork();
		if (cpid == -1)
		{
			printf("Program error, reason: %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}

		if (cpid != 0)
		{
			printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
			int serverfd, clientfd;
			struct sockaddr_in serverAdr, clientAdr;
			socklen_t clientAdr_size;

			serverfd = socket(AF_INET, SOCK_STREAM, 0);
			if (serverfd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			memset(&serverAdr, 0, sizeof(serverAdr));
			serverAdr.sin_family = AF_INET;
			serverAdr.sin_addr.s_addr = INADDR_ANY; 
			serverAdr.sin_port = htons(2048);

			if (bind(serverfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			if (listen(serverfd, 20)== -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			clientAdr_size = sizeof(clientAdr);

			while(state!=1);

			clock_gettime(CLOCK_MONOTONIC_RAW,&start);

			clientfd = accept(serverfd, (struct sockaddr *) &clientAdr,
							&clientAdr_size);
			if (clientfd == -1)
			{
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}
			while (ipc_p.st_data.index<times)
			{
				memcpy(ipc_p.full_data,&ipc_p.st_data.index,sizeof(int));
				write(clientfd,ipc_p.full_data,PIPE_BUF);
				ipc_p.st_data.index++;
			}
			while(state!=2) ;
			clock_gettime(CLOCK_MONOTONIC_RAW,&end);
			sec = end.tv_sec - start.tv_sec;
			nansec = end.tv_nsec - start.tv_nsec;
			printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
			close(clientfd);
			close(serverfd);
			exit(EXIT_SUCCESS);
		}
		else
		{
			printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);
			int sockfd;
			struct sockaddr_in serverAdr;
			socklen_t clientAdr_size;
			
			sockfd = socket(AF_INET, SOCK_STREAM, 0);
			if (sockfd == -1){
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			memset(&serverAdr, 0, sizeof(serverAdr));
			serverAdr.sin_family = AF_INET;
			serverAdr.sin_addr.s_addr = inet_addr("127.0.0.1"); 
			serverAdr.sin_port = htons(2048);

			if (connect(sockfd,(struct sockaddr *) &serverAdr,
					sizeof(serverAdr)) == -1){
				printf("Program error, reason: %s\n", strerror(errno));
				exit(EXIT_FAILURE);
			}

			kill(ppid,SIGUSR1);
			while (ipc_c.st_data.index<times-1)
			{
				read(sockfd,ipc_c.full_data,PIPE_BUF);
				memcpy(&ipc_c.st_data.index, ipc_c.full_data,sizeof(int));
			}
			kill(ppid,SIGUSR2);
			close(sockfd);
			exit(EXIT_SUCCESS);
		}

	}
	
	else
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

}