
;˵�����ó����ܶ����ļ�������16���Ƶ���ʽ��������Ǵ����������⣬�ҵ��Ժܾ�û�н��
;��һ��������ʱ�����ֻ��ԣ����Կ��ܻᰴ2�β��ܽ��ж�Ӧ���ܣ�Ԥ����show��������⡣
;�ڶ������һҳ������256���ַ���û�����ļ�����ʱ��ֹͣ�������������ַ���16������������ȷ��

.386
data segment use16
buffer db 25
       db ?
       db 25 dup(?) ;�����ļ���
handle dw 0 ;�����ļ����
buf    db 256 dup(0) ;ÿ�ζ���256���ֽ�
hex db "0123456789ABCDEF" ;ת����16��������׼������
s db "00000000:            |           |           |                             "
file_size db 4 dup(0) ;�ļ���С
offsets db 4 dup(0) ;ƫ����
byte_in_buf dw 0 ;����buf��Ķ����ֽ���
total_line dw 0
line dw 16
i db 0 ;����32λ��ת16���Ƶ�ʱ��ѭ������
j db 0 ;\����һ�е����16���������ַ�������
k db 0 ;/
input_info db "Please input filename:",0Dh,0Ah,'$' ;��ʾ������Ϣ
err_info db "Can't open the file",'$' ;��ʾ���ļ�ʧ����Ϣ
data ends
code segment use16
assume cs:code, ds:data

;���ļ�����
openfile:
    push eax
    push edx
    push edi
    push ecx
    mov ah,0Ah
    mov dx,offset buffer ;��ȡ���뵽���������ַ�
    int 21h
    mov ah,2
    mov dl,0Ah
    int 21h
    mov dx,offset buffer+2 ;��Ҫƫ��2λ���������ļ������׵�ַ
    mov di,2
openfile_change:
    inc di
    cmp buffer[di],0Dh ;�����ļ���������ǻس������滻��0����֤�ܴ��ļ�
    jne openfile_change
    mov buffer[di],0
    mov ah,3Dh ;���ļ�
    mov al,0
    int 21h
    jc openfile_error
    mov handle,ax ;���ļ����������handle��
    jmp openfile_done
openfile_error:
    mov dx,offset err_info ;�ļ���ʧ�ܣ���ʾ������Ϣ����������
    mov ah,9
    int 21h
    mov ah,4Ch
    int 21h
openfile_done:
    pop ecx
    pop edi
    pop edx
    pop eax
    ret

;�ļ���С����
filesize:
    push ax
    push bx
    push cx
    push dx
    mov ah,42h ;�ļ���С����
    mov al,2
    mov bx,handle
    mov cx,0
    mov dx,0
    int 21h
    mov word ptr file_size[2],dx
    mov word ptr file_size[0],ax ;���������file_size������
    pop dx
    pop cx
    pop bx
    pop ax
    ret

;8λ��ת����16���ƺ�����������si,ax
char2hex:
    push cx
    push di
    push bx
    push ax
    mov cl,4
    ror ax,cl ;ѭ������4λ��������4λ
    and ax,00Fh ;��00Fh���������Ϳ�����Ϊ�±���hex�������ҵ���Ӧ���ַ�
    mov di,ax
    mov bl,hex[di]
    mov s[si],bl ;�����������еĶ�Ӧλ��
    pop ax
    push ax
    and ax,00Fh ;������4λ
    mov di,ax
    mov bl,hex[di]
    inc si
    mov s[si],bl
    pop ax
    pop bx
    pop di
    pop cx
    ret

;32λ��ת����16��������������eax,si
long2hex:
    push eax
long2hex_loop:
    rol eax,8 ;��ѭ������8λ����8λ����ԭ���ĸ�8λ
    push eax
    and eax,0FFh ;����������Ϳ��Ե���char
    ;����char2hex����,�����������char2hex�в�ͬ
    push cx
    push di
    push bx
    push ax
    mov cl,4
    ror ax,cl
    and ax,00Fh
    mov di,ax
    mov bl,hex[di]
    mov cl,i
    mov ch,0
    shl cl,1 ;ʹ��cx��ȷ��si��ֵ
    mov si,cx
    mov s[si],bl
    pop ax
    push ax
    and ax,00Fh
    mov di,ax
    mov bl,hex[di]
    inc si
    mov s[si],bl
    pop ax
    pop bx
    pop di
    pop cx
    pop eax
    inc i ;32λһ�����Էֽ��4��char������ѭ����4��
    cmp i,4
    jne long2hex_loop
    mov i,0 ;�����i��eaxԭֵ
    pop eax
    ret

main:
   mov ax, data
   mov ds, ax
   mov dx,offset input_info ;������ʾ������Ϣ
   mov ah,9
   int 21h
   call openfile ;���ô��ļ�����
   mov ax,0B800h ;ʹ��ͼ��ģʽ
   mov es,ax
;����
clear:
   mov cx,80*16
   mov ax,0020h
   cld
   rep stosw 
;�ļ���С
   mov ah,42h
   mov al,2
   mov bx,handle
   mov cx,0
   mov dx,0
   int 21h
   call filesize ;�����ļ���С����
   mov ax,word ptr offsets[2]
go:
   xor ax,ax
   xor cx,cx ;��ʼ��
   mov ah,42h
   mov al,0  
   mov bx,handle
   mov cx,word ptr offsets[2]
   mov dx,word ptr offsets[0]
   int 21h ;���ļ�ָ��ָ����Ӧ��λ
   mov bx,word ptr file_size[0] 
   sub bx,word ptr offsets[0]
   cmp bx,256 ;�ļ��ܴ�С��ƫ�ƣ�С��256˵���ѵ����һҳ
   jb bytebuf
   mov word ptr byte_in_buf,256 ;����256�Ļ���������256���ֽ�
   jmp nexts
bytebuf:
   mov word ptr byte_in_buf,bx ;���һҳ��buf���ֽ�������bx�е���
nexts:
   mov ah, 3Fh
   mov bx, handle
   mov cx, byte_in_buf
   mov dx, offset buf
   int 21h ;���ļ�
   mov eax,dword ptr offsets[0] ;eax��ֵΪ32λoffsets�����ֵ
   mov di,0
   mov ecx,0
work:
   push eax
   call long2hex ;����long2hex��ƫ�Ƶ�ַת���ַ�
   mov si,10 ;��ʱsi�Ƶ�����ĵ�10λ
;��16������������s������
showhex:
   push si
   mov al,k 
   mov ah,0
   mov si,ax ;si�ĳ�ʼֵ��k������
   mov al,buf[si]
   pop si
   call char2hex ;����char2hex��ת��
   add j,1
   add k,1
   add si,2
   cmp si,56 ;��si��ֵ������ѭ��
   jb showhex
   mov si,59 ;�ƶ����鵽ascii����׵�ַ
   sub k,16 ;��ȥ16���¶���16����
   mov j,0
;��ascii�뱣����������
showascii:
   push si
   mov al,k
   mov ah,0
   mov si,ax
   mov al,buf[si]
   pop si
   mov s[si],al
   inc si
   inc j
   inc k
   cmp j,16 ;��j������ascii������
   jne showascii
   xor si,si ;��si����
;������ɫ
showcolor:
    mov ah,07h ;�������ַ�����Ϊ��ɫ
    mov al,s[si]
    cmp al,'|'
    je color
    jne show
color: 
    mov ah,0Fh ;��������Ϊ�����Ȱ�ɫ
show: 
    mov word ptr es:[di],ax
    inc si
    add di,2
    cmp si,75 ;һ������λ��
    jb showcolor    
    add di,10 ;������һ��
    pop eax
    add eax,16 ;һ����ʾ16������ƫ�Ƶ�ַ��16
    inc ecx
    mov j,0
    mov si,0
    cmp ecx,16 ;һ��16��
    jb work 
    mov ah,1 ;��ʾ
    int 21h
;����
key:
    xor ax,ax ;��ax����
    mov ah,0
    int 16h ;�ȴ����밴��
    cmp ax,4900h ;ǰһҳ
    je pageup
    cmp ax,5100h ;��һҳ
    je pagedown
    cmp ax,4700h ;home��
    je home_jump
    cmp ax,4F00h ;end��
    je end_jump 
    cmp ax,011Bh ;ESC��
    je done_jump
    jmp go ;���������������������go����ʾ����
;ǰһҳ
pageup:
    mov ax,word ptr offsets[0]
    sub ax,256 ;ƫ������256
    jc home_jump ;���������λ���൱�ڱ����home��
    mov word ptr offsets[0],ax ;�����µ�ƫ����
    jmp go
end_jump:
    jmp endk ;���������̫������������ת
done_jump:
    jmp done
home_jump:
    jmp home
;��һҳ
pagedown:
    mov ax,word ptr offsets[0]
    add ax,256 ;ƫ������256
    push ax
    sub ax,word ptr file_size[0] ;��������ļ���С���൱��end��
    jnc endk
    pop ax
    mov word ptr offsets[0],ax
    jmp go
;home��
home:
    xor ax,ax 
    mov word ptr offsets[0],ax ;����ax������offsets
    jmp go
;end��
endk:
    mov ax,word ptr file_size[0] ;��ֵ�ļ���С
    and ax,0FFh ;��������㲻Ϊ0��˵���е�����
    jnz one_line_end
    mov ax,256
one_line_end:
    push cx
    mov cx,ax
    mov ax,word ptr file_size[0]
    sub ax,cx
    pop cx
    mov word ptr offsets[0],ax
    jmp go
done:
   mov ah,3Eh
   mov bx, handle
   int 21h ;�ر��ļ�
   mov ah,15
   int 10h
   mov ah,0
   int 10h ;����
   mov ah, 4Ch
   int 21h
code ends
end main
