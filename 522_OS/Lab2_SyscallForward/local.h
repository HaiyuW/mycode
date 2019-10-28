#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <iostream>
#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <errno.h>

#define handle_error(msg) \
	do { printf("Program error, reason: %s\n", strerror(errno)); perror(msg); exit(EXIT_FAILURE); } while (0)

#define BUFSIZE 64
#define DATA_BUF_NUM 50
#define DATA_BUF_SIZE 1024*2048
struct redzone {
    char filename[BUFSIZE];
    int flags;
    mode_t mode;
    int fd;
    char* buf;
    int read_size;
    size_t count;
    char str_mat[DATA_BUF_NUM][DATA_BUF_SIZE];
    int index;
};
