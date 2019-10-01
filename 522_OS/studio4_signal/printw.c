#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>

/**
* Haiyu Wang modify some codes
* get sigint_handler the funtion of calling back message
* use write to print the message.
**/

#define size 5000

volatile int i;

int called [size];

void sigint_handler( int signum ){
        char *message = "handler called\n";
        int num_chars;
        num_chars = strlen(message);
        write(0,message,num_chars);

        called[i] = 1;

        return;
}

int main (int argc, char* argv[]){

        struct sigaction ss;

        ss.sa_handler = sigint_handler;
        ss.sa_flags = SA_RESTART;

        sigaction( SIGINT, &ss, NULL );

        for(i = 0; i < size; i++){
                printf("i: %d\n", i);
                usleep(1000); // wait for 1 msst
        }


        for(i = 0; i < size; i++){
                if( called[i] == 1 )
                        printf("%d was possibly interrupted\n", i);
        }

        return 0;
}
