#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
/**
* author: Haiyu Wang
* Usage: ./readfifo <filename>
*
* open read permission of the FIFO 
* print whatever it reads.
**/

// error checking
void err(char *str)
{
	perror(str);
	exit(EXIT_FAILURE);
}

int main (int argc, char *argv[])
{
	FILE *fp;
	char buf[255];

    // input format checking
	if (argc!=2){
		fprintf(stderr, "Usage: %s <string>\n", argv[0]);
		exit(EXIT_FAILURE);  
    }

    // open fifo
    fp = fopen(argv[1],"r");

    if (fp == NULL)
    	err("open err");

    // hold on reading and print what it reads
    while (1){
        while (fgets(buf,255,fp)!=NULL)
            printf(buf);
    }

    // close fifo
    fclose(fp);
    exit(EXIT_SUCCESS);
}