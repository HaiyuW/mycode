#include"encrypt_tools.h"
void encrypt_tools::encrypt(std::string m_path, std::string key_path)
{
    FILE *fp = NULL;
    fp = fopen(key_path,"r");
    for(int i=0;i<16;i++)
        for(int j=0;j<16;j++)
            fscanf(fp,"%x",this->input_key[i][j]);
    fclose(fp);
    this->init_key();
    this->expansion_key();
    /*translate letter to ASCII code*/
    fp = fopen(m_path,"r");
    int ch;
    int count = 0;
    while((ch = getc(fp)) != EOF)
    {
        this->buff[count] = ch;
        count++;
        if (count == 16)
        {
            this->ReadMessage();
            this->encrypt_mblock();
            this->printm(m_path);
        }
    }
    for(int i=count;i<16;i++) this->buff[i] = 0;
    this->ReadMessage();
    this->encrypt_mblock();
    this->printm(m_path);
    fclose(fp);
}
void encrypt_tools::init_key()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            this->key[i][j] = this->input_key[i][j];
}
void encrypt_tools::expansion_key()
{
    for(int i=4;i<44;i++)
        for (int j=0;j<4;j++)
        {
            int temp = this->key[j][i-1];
            if (i % 4 == 0)
                temp = this->func_g(j,i);
            this->key[j][i] = temp ^ this->key[j][i-4];
        }
}
int encrypt_tools::func_g(int a,int b)
{
    int temp = this->key[(a+1)%4][b-1];
    int up = temp / 16;
    int down = temp % 16;
    temp = this->s[up][down] ^ this->rcon[(b-4)+a];
    return temp;
}
void encrypt_tools::ReadMessage()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            this->m[j][i] = this->buff[i*4+j];
}
void encrypt_tools::encrypt_mblock()
{
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
}
void encrypt_tools::XorKey(int ii)
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
            m[i][j] ^= key[i][j+4*ii];
}
void encrypt_tools::SubMessage()
{
    for(int i=0;i<4;i++)
        for(int j=0;j<4;j++)
        {
            int up = m[i][j] / 16;
            int down = m[i][j] % 16;
            m[i][j] = s[up][down];
        }
}
void encrypt_tools::LeftShiftRows()
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
void encrypt_tools::MixColumns()
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
void encrypt_tools::printm(std::string m_path)
{
    FILE *fpout = fopen(m_path.insert(m_path.indexOf(".txt"),"_encrypt"),"w");
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            if (m[j][i]<0x10) fprintf(fpout,"0");
            fprintf(fpout,"%x",m[j][i]);
        }
    }
    fclose(fpout);
}
