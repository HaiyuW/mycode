#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

/**
 * Author: Haiyu Wang
 * The example of maunal page of "pipe" attributes to this code
 * Usage: ./pipe <string>
 *
 * Description: create a pipe and fork a child process. Parent write strings 
 * and child read them and print them out 
 *
 **/

// err function
void err(const char *str)
{
        perror(str);
        exit(EXIT_FAILURE);
}

int main(int argc, char *argv[])
{
        int pipefd[2];
        pid_t pid;
        char buf;
        int length;

        // wrong input
        if (argc!=2){
                fprintf(stderr, "Usage: %s <string>\n", argv[0]);
                exit(EXIT_FAILURE);
        }

        // fail to open pipe
        if (pipe(pipefd)==-1)
                err("pipe");

        // fork a child process
        pid = fork();;
        // fail to fork
        if (pid == -1)
                err("fork");

        length = strlen(argv[1]);
        
        // parent write
        if (pid != 0){
                close(pipefd[0]); // close unused read end
                write(pipefd[1],argv[1],length);
                close(pipefd[1]); // close write end
                wait(NULL); // wait for child
                _exit(EXIT_SUCCESS);
        }

        //child read and print
        else{
                close(pipefd[1]); //close unused write end
                while(read(pipefd[0],&buf,1)>0)
                        write(STDOUT_FILENO,&buf,1); // read and print
                write(STDOUT_FILENO,"\n",1); // print a line break
                close(pipefd[0]); // close read end
                exit(EXIT_SUCCESS);
        }
}
