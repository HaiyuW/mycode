#ifndef M_ENCRYPT_H
#define M_ENCRYPT_H

#include<iostream>
#include<iomanip>
#include<string>
struct QuoRemain{
    int quotient;
    int remainder;
};
char HEX[16] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
int s[16][16];
int cons[16][16];
int m[4][4] = {{0x00,0x44,0x88,0xcc},{0x11,0x55,0x99,0xdd},{0x22,0x66,0xaa,0xee},{0x33,0x77,0xbb,0xff}};
int last_cipher[4][4] = {{0x08,0x04,0x00,0xcc},{0x07,0x03,0x99,0xdd},{0x06,0x02,0xaa,0xee},{0x05,0x01,0xbb,0xff}};
int cipher[4][4] = {{0x08,0x04,0x00,0xcc},{0x07,0x03,0x99,0xdd},{0x06,0x02,0xaa,0xee},{0x05,0x01,0xbb,0xff}};
int col[4][4] = {{2,3,1,1},{1,2,3,1},{1,1,2,3},{3,1,1,2}};
int concol[4][4] = {{0x0e,0x0b,0x0d,0x09},{0x09,0x0e,0x0b,0x0d},{0x0d,0x09,0x0e,0x0b},{0x0b,0x0d,0x09,0x0e}};
int key[4][44] = {0};
int rcon[40] = {0};
int input_key[4][4] = {{0x00,0x04,0x08,0x0c},{0x01,0x05,0x09,0x0d},{0x02,0x6,0x0a,0x0e},{0x03,0x07,0x0b,0x0f}};
char buff[33];
int cbc_mode = 1;
int cbc_vector[4][4] = {{0x08,0x04,0x00,0xcc},{0x07,0x03,0x99,0xdd},{0x06,0x02,0xaa,0xee},{0x05,0x01,0xbb,0xff}};

int AESencrypt();
void init_Sbox();
void build_Sbox();
int SearchForIE(int a,int b);			//search for inverse element in GF(2^8)
int CircleRightShift(int a,int b);		//shift by bit when build S_box
int minus(int x,int y);					//minus in GF(2^8)
int multiply(int x,int y);				//multiply in GF(2^8)
QuoRemain divide(int x,int y);			//divide in GF(2^8) return quotient and remainder
int getbit(int x);						//get the bits of x

void LeftShiftRows();					//shift rows
void RightShiftRows();					//con-shift rows
void MixColumns();
void conMixColumns();
void SubMessage();
void conSubMessage();
void XorKey(int ii);

int func_g(int a,int b);
void init_key();
void expansion_key();
void printm();
void de_printm();

int CharToHex(char a);	//translate char to HEX-int
void ReadMessage();		//translate one demensional vector to matrix,meanwhile translate char to HEX-int
void encrypt();
void decrypt();
void cbc();
void cipher_update();
void last_cipher_update();

#endif // M_ENCRYPT_H


