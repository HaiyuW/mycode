#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
/**
* author: Haiyu Wang
* Usage: ./read <fifoname>
*
* open read permission of the FIFO 
* use fscanf to read and print the origin value 
* double the input and print.
* open write stream to prevent FIFO from being closed
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
    FILE *writefp;
    char buf;
    int i=0;

    // input format checking
    if (argc!=2){
        fprintf(stderr, "Usage: %s <string>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // open read stream
    fp = fopen(argv[1],"r");

    if (fp == NULL)
        err("open err");

    // open write stream
    writefp = fopen(argv[1],"w");
    while (1){
        // read the inputs 
        while(fscanf(fp,"%d",&i)!=EOF){
            printf("%d\n",i); // print origin value  
            printf("%d\n", i*2);// double it and print
        }
    }

    fclose(fp);
    fclose(writefp);
    exit(EXIT_SUCCESS);
}
~                                                                             
-- 插入 --                                                  47,1         全部
