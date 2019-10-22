int AESencrypt()
{
    FILE *fp = NULL;

    /*init_output*/
    fp = fopen("data/encrypt_ciphertext.txt","w");
    fclose(fp);
    fp = fopen("data/decrypt_HEXtext.txt","w");
    fclose(fp);

    /*translate letter to ASCII code*/
    fp = fopen("data/encrypt_plaintext.txt","r");
    FILE *fpout = NULL;
    fpout = fopen("data/encrypt_HEXtext.txt","w");
    int ch;
    int count = 0;
    while((ch = getc(fp)) != EOF)
    {
        count++;
        int up = ch/16;
        int down = ch % 16;
        putc(HEX[up],fpout);
        putc(HEX[down],fpout);
        if (count == 16)
        {
            putc('\n',fpout);
            count = 0;
        }
    }
    fclose(fp);
    fclose(fpout);

    /*input key*/
    fp = fopen("data/key.txt","r");
    fgets(buff,33,(FILE*)fp);
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            input_key[j][i] = CharToHex(buff[(i*4+j)*2])*16+CharToHex(buff[(i*4+j)*2+1]);
    fclose(fp);

    init_Sbox();
    build_Sbox();
    init_key();
    expansion_key();

    /*encrypt block-message*/
    char *flag;
    fp = fopen("data/encrypt_HEXtext.txt","r");
        /*divide message to 4x4 matrix block*/
    while ((flag = fgets(buff,34,(FILE*)fp)) != NULL)
    {
        for(int i=0;i<32;i++)
            if (buff[i] == '\0')
            {
                std::cout << "!" << std::endl;
                for(int j=i+1;j<32;j++)
                buff[j] = '0';
                break;
            }
        ReadMessage();
        encrypt();
        printm();
    }
    fclose(fp);

    /*init_cipher*/
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            last_cipher[i][j] = cbc_vector[i][j];
    /*decrypt block-message*/
    fp = fopen("data/decrypt_ciphertext.txt","r");
        /*divide ciphertext to 4x4 matrix block*/
    while ((flag = fgets(buff,34,(FILE*)fp)) != NULL)
    {
        ReadMessage();
        decrypt();
        de_printm();
    }
    fclose(fp);

    /*translate ASCII code to letter*/
    fp = fopen("data/decrypt_HEXtext.txt","r");
    fpout = fopen("data/decrypt_plaintext.txt","w");

    count = 0;
    while((ch = getc(fp)) != EOF)
    {
        count++;
        int temp = 0;
        if ((ch<='f') && (ch>='a')) temp += ch-'a'+10;
        else if ((ch<='9') && (ch>='0')) temp += ch-'0';
        temp *= 16;
        ch = getc(fp);
        if ((ch<='f') && (ch>='a')) temp += ch-'a'+10;
        else if ((ch<='9') && (ch>='0')) temp += ch-'0';
        putc((char)temp,fpout);
        if (count == 16)
        {
            ch = getc(fp);		//ch = '\n'
            count = 0;
        }

    }
    fclose(fp);
    fclose(fpout);
    return 0;


}
void init_Sbox()
{
    for(int i=0;i<16;i++)
        for(int j=0;j<16;j++)
            s[i][j] = i*16+j;
}
void build_Sbox()
{
    for(int i=0;i<16;i++)
        for(int j=0;j<16;j++)
        {
            s[i][j] = SearchForIE(s[i][j],0x11b);
            int c = 0x63;
            c = c ^ s[i][j] ^ CircleRightShift(s[i][j],4) ^ CircleRightShift(s[i][j],5) ^ CircleRightShift(s[i][j],6) ^ CircleRightShift(s[i][j],7);
            s[i][j] = c;

            int up = s[i][j] / 16;
            int down = s[i][j] % 16;
            cons[up][down] = i*16+j;

        }
}
int SearchForIE(int a,int b)
{
    int w_last = 0;
    int w_now  = 1;
    int v_last = 1;
    int v_now  = 0;
    int r_last = a;
    int r_now  = b;
    int q      = 0;
    while(r_now != 0)
    {
        QuoRemain divResult;
        divResult = divide(r_last,r_now);

        q = divResult.quotient;
        int temp = divResult.remainder;
        r_last = r_now;
        r_now = temp;
        temp = minus(v_last,multiply(q,v_now));
        v_last = v_now;
        v_now = temp;
        temp = minus(w_last,multiply(q,w_now));
        w_last = w_now;
        w_now = temp;
    }
    return v_last;
}
int minus(int x,int y)
{
    int result;
    result = x ^ y;
    return result;
}
int multiply(int x,int y)
{
    int a = x;
    int b = y;
    int result = 0;
    for(int i=0;i<8;i++)
    {
        int temp = b * (a & 1);

        temp <<= i;
        result = result ^ temp;
        a >>= 1;
    }
    if (result>0xff)
    {
        QuoRemain temp;
        temp = divide(result,0x11b);
        result = temp.remainder;
    }
    return result;
}
QuoRemain divide(int x,int y)
{
    int a = x;
    int b = y;
    QuoRemain result =
    {
        .quotient = 0,
        .remainder = 0
    };
    int len1 = getbit(a);
    int len2 = getbit(b);
    int len3 = len1-len2;
    if (a<b)
    {
        if (len3 == 0)
        {
            result.quotient = 1;
            result.remainder = a^b;
            return result;
        }
        else
        {
            result.quotient = a;
            result.remainder = a;
            return result;
        }
    }
    int top_bit = 1;
    top_bit <<= (len1-1);
    b <<= len3;
    for(int i=0;i<len3;i++)
    {
        result.quotient <<= 1;
        if ((top_bit & a) != 0)
        {
            result.quotient ^= 1;
            a ^= b;
        }
        top_bit >>= 1;
        b >>= 1;
    }
    result.quotient <<= 1;
    if (getbit(a)<getbit(b))
        result.remainder = a;
    else
    {
        result.quotient |= 1;
        result.remainder = a^b;
    }
    return result;
}
int getbit(int x)
{
    int result = 0;
    int temp = x;
    while(temp!=0)
    {
        temp >>= 1;
        result++;
    }
    return result;
}
int CircleRightShift(int a,int b)
{
    int result = ((a % (1<<b))<<(8-b)) + (a>>b);
    return result;
}
void LeftShiftRows()
{
    for(int i=1;i<4;i++)
    {
        int temp[4];
        for(int j=0;j<4;j++)
            temp[j] = m[i][(j+i) % 4];
        for(int j=0;j<4;j++)
            m[i][j] = temp[j];
    }
}
void RightShiftRows()
{
    for(int i=1;i<4;i++)
    {
        int temp[4];
        for(int j=0;j<4;j++)
            temp[j] = m[i][(j-i+4) % 4];
        for(int j=0;j<4;j++)
            m[i][j] = temp[j];
    }
}
void MixColumns()
{
    int temp[4][4];
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            int result = 0;
            for(int k=0;k<4;k++)
                result ^= multiply(col[i][k],m[k][j]);
            temp[i][j] = result;
        }
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            m[i][j] = temp[i][j];

}
void conMixColumns()
{
    int temp[4][4];
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            int result = 0;
            for(int k=0;k<4;k++)
                result ^= multiply(concol[i][k],m[k][j]);
            temp[i][j] = result;
        }
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            m[i][j] = temp[i][j];
}
void init_key()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            key[i][j] = input_key[i][j];
    rcon[0] = 1;
    for(int i=1;i<10;i++)
        rcon[i*4] = multiply(rcon[(i-1)*4],2);
}
void expansion_key()
{
    for(int i=4;i<44;i++)
        for (int j=0;j<4;j++)
        {
            int temp = key[j][i-1];
            if (i % 4 == 0)
                temp = func_g(j,i);
            key[j][i] = temp ^ key[j][i-4];
        }
}
int func_g(int a,int b)
{
    int temp = key[(a+1)%4][b-1];
    int up = temp / 16;
    int down = temp % 16;
    temp = s[up][down] ^ rcon[(b-4)+a];
    return temp;
}
void printm()
{
    FILE *fp = fopen("data/encrypt_ciphertext.txt","a");
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            if (m[j][i]<0x10) fprintf(fp,"0");
            fprintf(fp,"%x",m[j][i]);
        }

    }
    fprintf(fp,"\n");
    fclose(fp);
}
void de_printm()
{
    FILE *fp = fopen("data/decrypt_HEXtext.txt","a");
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            if (m[j][i]<0x10) fprintf(fp,"0");
            fprintf(fp,"%x",m[j][i]);
        }
    }
    fprintf(fp,"\n");
    fclose(fp);
}
void SubMessage()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            int up = m[i][j] / 16;
            int down = m[i][j] % 16;
            m[i][j] = s[up][down];
        }
}
void conSubMessage()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            int up = m[i][j] / 16;
            int down = m[i][j] % 16;
            m[i][j] = cons[up][down];
        }
}
void XorKey(int ii)
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            m[i][j] ^= key[i][j+4*ii];
}
int CharToHex(char a)
{
    int result = 0;
    if ((a<='f') && (a>='a'))
        result = a-'a'+10;
    else if ((a<='F') && (a>='A'))
        result = a-'A'+10;
    else if ((a<='9') && (a>='0'))
        result = a-'0';
    return result;
}
void ReadMessage()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            m[j][i] = CharToHex(buff[(i*4+j)*2])*16+CharToHex(buff[(i*4+j)*2+1]);
        }

}
void encrypt()
{
    if (cbc_mode) cbc();
    XorKey(0);
    for(int ii = 1;ii<10;ii++)
    {
        SubMessage();
        LeftShiftRows();
        MixColumns();
        XorKey(ii);
    }
    {
        int ii = 10;
        SubMessage();
        LeftShiftRows();
        XorKey(ii);
    }
    if (cbc_mode)
    {
        cipher_update();
        last_cipher_update();
    }
}
void decrypt()
{

    if (cbc_mode) cipher_update();
    XorKey(10);
    for(int ii = 1;ii<10;ii++)
    {
        RightShiftRows();
        conSubMessage();
        XorKey(10-ii);
        conMixColumns();
    }
    {
        int ii = 10;
        RightShiftRows();
        conSubMessage();
        XorKey(10-ii);
    }
    if (cbc_mode)
    {
        cbc();
        last_cipher_update();
    }

}
void cbc()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            m[i][j] ^= last_cipher[i][j];
}
void cipher_update()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            cipher[i][j] = m[i][j];
}
void last_cipher_update()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            last_cipher[i][j] = cipher[i][j];
}
