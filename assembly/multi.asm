.386
data1 segment use16
buffer db 25
       db ?
       db 25 dup(?);��������ĵ�һ���ַ���
buff db 25
     db ?
     db 25 dup(?);��������ĵڶ����ַ���
i dw 0;�����һ�������ַ�����
j dw 0;����ڶ��������ַ�����
a dw 0;�����һ����
b dw 0;����ڶ�����
higher dw 0;�˻��ĸ�16λ
lower dw 0;�˻��ĵ�16λ
s db 10 dup(0),0Dh,0Ah,'$';����16���Ƶ�����
k db 10 dup(' '),0Dh,0Ah,'$';����10���Ƶ�����
data1 ends

stack1 segment stack use16
dw 100h dup(?)
stack1 ends

code segment use16
assume cs:code, ds:data1, ss:stack1
start:
    mov ax,data1
    mov ds,ax
    mov ah,0Ah;����10�Ź��ܱ���������뵽�����е��ַ�
    mov dx,offset buffer
    int 21h
    mov ah,2
    mov dl,0Dh;�س�
    int 21h
    mov ah,2
    mov dl,0Ah;����
    int 21h
    xor cx,cx;����������
    mov cl,buffer[1];�����ַ�����
    ;jcxz done;���δ�����ַ���ֱ�ӽ���
    mov i,cx;��i�����һ���ַ�����
    mov cx,0
    mov bx,offset buffer+2;bxָ���һ���ַ�
    mov ah,0Ah;�ڶ����ַ�����ͬ��
    mov dx,offset buff
    int 21h
    mov ah,2
    mov dl,0Dh
    int 21h
    mov ah,2
    mov dl,0Ah
    int 21h
    xor cx,cx
    mov cl,buff[1]
    ;jcxz done
    mov j,cx
    mov di,offset buff+2
    mov cx,i
againa:
    mov ah,2
    mov dl,[bx];���ַ�����ʽһ���������һ���ַ���
    inc bx
    int 21h
    loop againa
output1:
    mov dl,'*';���*��
    mov ah,02h
    int 21h
    mov cx,j
againb:
    mov ah,2
    mov dl,[di];����ڶ����ַ���
    inc di
    int 21h
    loop againb
output2:
    mov dl,'=';���=��
    mov ah,02h
    int 21h
    mov ah,2
    mov dl,0Dh
    int 21h
    mov ah,2
    mov dl,0Ah;�س�����
    int 21h
;�������ַ���ת��Ϊ���ֱ𱣴���a,b��
tranforma:
    mov bx,offset buffer+2
    mov ax,0
againa1:
    mov cx,0
    mov cl,buffer[bx];�����һ���ַ�
    mov si,10;10���ƣ�����ÿ�γ�10
    mul si
    sub cl,'0'
    add ax,cx
    inc bx
    sub i,1;��i��ʾλ��������ѭ��
    jnz againa1

;�ڶ����ַ�����ͬ��
transformb:
    mov a,ax
    mov di,offset buff+2
    mov ax,0
againb2:
    mov cx,0
    mov cl,[di]
    mov si,10
    mul si
    sub cl,'0'
    add ax,cx
    inc di
    sub j,1
    jnz againb2
;���г˷����㣬��16λ��dx����16λ��ax����higher��lower���ֱ𱣴�
cal:
    mov bx,a
    mul bx
    mov higher,dx
    mov lower,ax
;���ת��Ϊ10���Ʋ����
transformD:
    mov eax,higher;�ȸ�eax�ĵ�16λ��ֵhigher
    shl eax,16;�����ƶ�16λ
    mov eax,lower;�ٸ���16λ��ֵlower
    mov di,0;�����±�
    mov cx,0;ͳ��push����
againD:
    mov edx,0;32λ��������������edx:eax
    mov ebx,10;����ebxΪ0
    div ebx
    add dl,'0';ת����ASCII��push��ջ
    push dx
    inc cx
    cmp eax,0;��eaxΪ0����ѭ��
    jne againD
pop_againD:
    pop dx
    mov k[di],dl;��ջ������pop�������������
    inc di
    dec cx;��cxΪ����ѭ��
    jnz pop_againD

    mov ah,9
    mov dx,offset k;��ʾ���鱣����ַ���
    int 21h

;16���Ƶ����
    mov cx,4
    mov di,0;s������±�
    mov bx,higher;�������16λ
outputh:    
    push cx
    mov cl,4;ÿ��ѭ������4λ
    rol bx,cl
    push bx
    and bx,000Fh
    cmp bx,10
    jb is_digit1
;������ַ�
is_alpha1:
    sub bl,10
    add bl,'A'
    jmp finish_4bits1
;���������
is_digit1:
    add bl,'0'
finish_4bits1:
    mov s[di],bl;�Ѹ�16λ�����ֱ��浽����
    pop bx
    pop cx
    add di,1
    sub cx,1
    jnz outputh
    mov cx,4
    mov di,4
    mov bx,lower;���е�16λ�����ֵ�ת������
outputh2:
    push cx
    mov cl,4
    rol bx,cl
    push bx
    and bx,000Fh
    cmp bx,10
    jb is_digit2
is_alpha2:
    sub bl,10
    add bl,'A'
    jmp finish_4bits2
is_digit2:
    add bl,'0'
finish_4bits2:
    mov s[di],bl
    pop bx
    pop cx
    add di,1
    sub cx,1
    jnz outputh2
    mov s[di],' '
    add di,1
    mov s[di],'h';���������� h��
    mov ah,09h
    mov dx,offset s;����ַ���
    int 21h

;�����Ƶ�ת�����
    push ax
    mov bx,higher
    mov i,4;ÿ��16λ����4����i������
    mov cl,4;4λ��������Ϊһ��
outputb1:
    rol bx,1;ÿ��ѭ������1λ
    mov dl,0
    adc dl,'0';adc������add�Ļ����ϼ���CF��־λ��ֵ
    mov ah,02h;��ʾ�ַ�
    int 21h
    sub cl,1
    jnz outputb1
space1:
    mov ah,02h;����ո�
    mov dl,' '
    int 21h
    mov cl,4
    sub i,1;
    jnz outputb1;����outputb1�������
    pop ax
;���е�16λ��ת�����
    mov bx,lower;
    mov cl,4
    mov j,4
outputb2:
    rol bx,1
    mov dl,0
    adc dl,'0'
    mov ah,02h
    int 21h
    sub cl,1
    jnz outputb2
space2:
    mov ah,02h
    mov dl,' ';����ո�
    int 21h
    mov cl,4
    sub j,1
    jnz outputb2
    mov ah,02h
    mov dl,'B';���B
    int 21h
    mov ah,2
    mov dl,0Dh
    int 21h
    mov ah,2
    mov dl,0Ah;�س�����
    int 21h
done:
    mov ah,4Ch
    int 21h
code ends
end start