data segment
i db 0				
j dw 0				
s db 4 dup(0),0Dh,0Ah,'$';�������������ת����16������
data ends
code segment
assume cs:code,ds:data
main:
    mov ax,data
    mov ds,ax
    mov ax,0B800h;�ı�ģʽ
    mov es,ax    
    mov cx,00FFh;�����256��Ԫ��
    mov dl,0
    mov di,0 
    mov j,0
    mov i,25;��ѭ����һ��25��Ԫ��
again:
    mov al,dl;��ʾdl��Ӧ��ASCII��
    mov ah,0Ch;��ɫΪ����ɫ
    mov word ptr es:[di],ax
    push ax
    push di
    push cx;���������Ĵ�����������ջ
change:
    mov ax,dx;��dl��ֵ����al
    push dx
    mov cx,4;ת����16����ʱ����λΪ4
    mov di,0;��ʾs�����±�
again2:
    push cx;����cx��ֵ����Ϊ����Ҫ��cx����������ı��ֵ
    mov cl,4
    rol ax,cl
    push ax
    and ax, 0Fh
    cmp ax, 10
    jb is_digit;���axֵС��10��Ϊ���֣���֮Ϊ��ĸ
is_alpha:
    sub al, 10
    add al, 'A'
    jmp finish_4bits
is_digit:
    add al, '0'
finish_4bits:
    mov s[di], al;��al��ֵ�����������У�256��Ԫ�����ֻ����λ�����棬���Ա�����s[2],s[3]
    pop ax
    pop cx
    add di, 1
    sub cx, 1
    jnz again2
    mov al,s[2];�ֱ��������Ĵ���ax,bx���洢��λ��16������
    mov ah,0Ah
    mov bl,s[3]
    mov bh,0Ah
    pop dx
    pop cx
    pop di;��ԭ�Ĵ�����ֵ��di�Ĵ�������Ҫ����һ��֮ǰ��ԭ����Ϊ����Ҫ�õ�di��ֵ
    mov word ptr es:[di+2],ax
    mov word ptr es:[di+4],bx;�ں���ֱ�洢ax,bx��ֵ��ע��Ҫ+2,��Ϊ��ɫ�ַ���ռһλ
    pop ax
    add dl,1;ASCII���1
    add di,160;Ϊ������һ�У�di��Ҫ��160(һ����160λ)
    sub cx,1;��cx��ֵ������ѭ������
    jz show
    sub i,1
    jnz again
next_row:
    add j,14;ȷ���м��
    mov di,j;ȷ����һ�е���ʼλ��
    mov i,25
    jmp again
show:
    mov al,'F'
    mov ah,0Ah
    mov bl,'F'
    mov bh,0Ah
    mov word ptr es:[di+2],ax
    mov word ptr es:[di+4],bx
    mov ah,1
    int 21h
    mov ah,4Ch
    int 21h
code ends
end main