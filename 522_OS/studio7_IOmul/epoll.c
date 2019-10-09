#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <sys/epoll.h>

#define TIMEOUT 5
#define BUF_LEN 512

/**
*
* Haiyu Wang  
*
* Usage: ./epoll
*
* test epoll with 2 kinds of mode
* level-triggered and edge-triggered 
*
*/

int main(int argc, char *args[])
{
  int epfd, nfds;
  int i;

  struct epoll_event ev,events[5];
  epfd = epoll_create1(0);
  ev.data.fd = STDIN_FILENO;
  ev.events = EPOLLIN;
  //ev.events = EPOLLIN|EPOLLET
  epoll_ctl(epfd, EPOLL_CTL_ADD, STDIN_FILENO, &ev);
  while(1)
  {
    nfds = epoll_wait(epfd, events, 5, -1);
    for (i=0;i<nfds;i++)
    {
      if (events[i].data.fd == STDIN_FILENO)
        printf("Data is available\n");
    }
  }
}