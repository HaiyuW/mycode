/******************************************************************************
 * * 
 * * dense_mm.c
 * * 
 * * This program implements a dense matrix multiply and can be used as a
 * * hypothetical workload. 
 * *
 * * Usage: This program takes a single input describing the size of the matrices
 * *        to multiply. For an input of size N, it computes A*B = C where each
 * *        of A, B, and C are matrices of size N*N. Matrices A and B are filled
 * *        with random values. 
 * *
 * * Written Sept 6, 2015 by David Ferry
 * ******************************************************************************/


/**
* Haiyu Wang modify some codes
* override SIGINT handler, call back infomation and count the times
**/

#include <stdio.h>  //For printf()
#include <stdlib.h> //for exit() and atoi()
#include<sys/types.h>
#include<sys/stat.h>
#include<unistd.h>
#include<fcntl.h>
#include<errno.h>
#include<string.h>
#include<signal.h>

const int num_expected_args = 2;
const unsigned sqrt_of_UINT32_MAX = 65536;
int count = 0; // count the times of handler()

// handler function
void handler (int arg)
{
	printf(" receive a SIGINT %d\n", arg);
	count++;
}

int main( int argc, char* argv[] ){

	unsigned index, row, col; //loop indicies
	unsigned matrix_size, squared_size;
	double *A, *B, *C;

	// declare struct sigaction 
	struct sigaction mysig;
	mysig.sa_handler = handler;
	mysig.sa_flags = SA_RESTART;
	
	// register handler function
	sigaction(SIGINT, &mysig,NULL);

	if( argc != num_expected_args ){
		printf("Usage: ./dense_mm <size of matrices>\n");
		exit(-1);
	}

	matrix_size = atoi(argv[1]);
	
	if( matrix_size > sqrt_of_UINT32_MAX ){
		printf("ERROR: Matrix size must be between zero and 65536!\n");
		exit(-1);
	}

	squared_size = matrix_size * matrix_size;

	printf("Generating matrices...\n");

	A = (double*) malloc( sizeof(double) * squared_size );
	B = (double*) malloc( sizeof(double) * squared_size );
	C = (double*) malloc( sizeof(double) * squared_size );

	for( index = 0; index < squared_size; index++ ){
		A[index] = (double) rand();
		B[index] = (double) rand();
		C[index] = 0.0;
	}


	printf("Multiplying matrices...\n");

	for( row = 0; row < matrix_size; row++ ){
		for( col = 0; col < matrix_size; col++ ){
			for( index = 0; index < matrix_size; index++){
			C[row*matrix_size + col] += A[row*matrix_size + index] *B[index*matrix_size + col];
			}	
		}
	}

	printf("Multiplication done!\n");
	printf("SIGINT times: %d\n", count);

	return 0;
}

