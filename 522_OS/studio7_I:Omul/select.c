#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

#define TIMEOUT 5
#define BUF_LEN 512

/**
*
* Haiyu Wang  
*
* Usage: ./select
*
* use select to monitor STDIN
* print what it reads. 
*
*/


int main(int argc, char *args[])
{
  struct timeval tv;
  fd_set readfds,testfds;
  int ret;

  tv.tv_sec = TIMEOUT;
  tv.tv_usec = 0;
  FD_ZERO(&readfds);
  // set STDIN_FILENO in the fd_set
  FD_SET(STDIN_FILENO, &readfds);

  while(1){
    testfds = readfds;
    ret = select (STDIN_FILENO + 1, &testfds, NULL, NULL, &tv);

    if (ret == -1){
      perror("select");
      return 1;
    }
    else if (!ret){
      printf("%d seconds elapsed.\n", TIMEOUT);
      return 0;
    }

    if (FD_ISSET(STDIN_FILENO,&readfds)){
      char buf[BUF_LEN+1];
      int len;
      len = read(STDIN_FILENO, buf, BUF_LEN);
      if (len == -1){
        perror("read");
        return 1;
      }
      if (len == 0)
        return 0;
      
      buf[len] = '\0';
      printf("read: %s", buf);

    }
   
  } 
}