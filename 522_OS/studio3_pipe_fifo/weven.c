#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

/**
* author: Haiyu Wang
* Usage: ./wodd <filename> <odd integer>
*
* open write permission of the FIFO 
* write odd integers to FIFO
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
	int value;

    // checking input format
	if (argc!=3){
		fprintf(stderr, "Usage: %s <string> <even int>\n", argv[0]);
		exit(EXIT_FAILURE);  
    }

    fp = fopen(argv[1],"w");

    // open write stream
    if (fp == NULL)
    	err("open error");


    // trans string to integer
    value = atoi(argv[2]);

    // if the integer is even, write it.
    if (value%2==0)
        fprintf(fp, "%d\n",value);
    // else, print error
    else{
        fprintf(stderr, "Not an Even Integer\n", argv[2]);
        exit(EXIT_FAILURE);
    }

    fclose(fp);
    _exit(EXIT_SUCCESS);
}