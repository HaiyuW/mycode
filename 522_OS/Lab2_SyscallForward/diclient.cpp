#include "local.h"

#define READ_SIZE 1024*8
#define TIMES 1000

using namespace std;
int main(int argc, char *args[])
{
    int readfd = open("ditest",O_RDWR|O_NONBLOCK, 0);
    int i;
    long sec, nansec;
    char buf[TIMES][READ_SIZE];
    struct timespec start = {0,0};	
	struct timespec end = {0,0};
    clock_gettime(CLOCK_MONOTONIC_RAW,&start);
    for (i=0;i<TIMES;i++)
    {
        read(readfd,buf[i],READ_SIZE);
        write(readfd,buf[i],READ_SIZE);
    }
    clock_gettime(CLOCK_MONOTONIC_RAW,&end);	
    sec = end.tv_sec - start.tv_sec;
    nansec = end.tv_nsec - start.tv_nsec;
    printf("time is %d seconds and %ld nanoseconds\n", sec, nansec);
    close(readfd);
}
