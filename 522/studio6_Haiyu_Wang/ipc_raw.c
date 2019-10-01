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

unsigned int times;
char ipc_type[100];

int main (int argc, char *argv[  ]){

	pid_t pid;

	if (argc != 3){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	times = atoi(argv[1]);
	strcpy(ipc_type,argv[2]);
	pid = fork();
	
	if (pid == -1)
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}
	else if (pid>0)
		printf("It's parent, process times: %d, IPC type: %s\n", times, ipc_type);
	else
		printf("It's child, process times: %d, IPC type: %s\n", times, ipc_type);

	return 0;


}