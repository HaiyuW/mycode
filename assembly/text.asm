
;说明：该程序能读入文件，并以16进制的形式输出。但是存在两个问题，我调试很久没有解决
;第一，按键的时候会出现回显，所以可能会按2次才能进行对应功能，预估是show块出了问题。
;第二，最后一页依旧是256个字符，没有在文件结束时就停止输出。但是输出字符和16进制数都是正确的

.386
data segment use16
buffer db 25
       db ?
       db 25 dup(?) ;储存文件名
handle dw 0 ;储存文件句柄
buf    db 256 dup(0) ;每次读入256个字节
hex db "0123456789ABCDEF" ;转换成16进制数的准备数组
s db "00000000:            |           |           |                             "
file_size db 4 dup(0) ;文件大小
offsets db 4 dup(0) ;偏移量
byte_in_buf dw 0 ;储存buf里的读入字节数
total_line dw 0
line dw 16
i db 0 ;控制32位数转16进制的时候循环次数
j db 0 ;\控制一行的输出16进制数和字符的数量
k db 0 ;/
input_info db "Please input filename:",0Dh,0Ah,'$' ;提示输入信息
err_info db "Can't open the file",'$' ;提示打开文件失败信息
data ends
code segment use16
assume cs:code, ds:data

;打开文件函数
openfile:
    push eax
    push edx
    push edi
    push ecx
    mov ah,0Ah
    mov dx,offset buffer ;提取输入到缓冲区的字符
    int 21h
    mov ah,2
    mov dl,0Ah
    int 21h
    mov dx,offset buffer+2 ;需要偏移2位才是输入文件名的首地址
    mov di,2
openfile_change:
    inc di
    cmp buffer[di],0Dh ;遍历文件名，如果是回车符就替换成0，保证能打开文件
    jne openfile_change
    mov buffer[di],0
    mov ah,3Dh ;打开文件
    mov al,0
    int 21h
    jc openfile_error
    mov handle,ax ;把文件句柄保存在handle中
    jmp openfile_done
openfile_error:
    mov dx,offset err_info ;文件打开失败，提示错误信息并结束程序
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

;文件大小函数
filesize:
    push ax
    push bx
    push cx
    push dx
    mov ah,42h ;文件大小功能
    mov al,2
    mov bx,handle
    mov cx,0
    mov dx,0
    int 21h
    mov word ptr file_size[2],dx
    mov word ptr file_size[0],ax ;结果保存在file_size数组中
    pop dx
    pop cx
    pop bx
    pop ax
    ret

;8位数转化成16进制函数，参数是si,ax
char2hex:
    push cx
    push di
    push bx
    push ax
    mov cl,4
    ror ax,cl ;循环右移4位，保留高4位
    and ax,00Fh ;和00Fh做与运算后就可以作为下标在hex数组中找到对应的字符
    mov di,ax
    mov bl,hex[di]
    mov s[si],bl ;保存在数组中的对应位置
    pop ax
    push ax
    and ax,00Fh ;保留低4位
    mov di,ax
    mov bl,hex[di]
    inc si
    mov s[si],bl
    pop ax
    pop bx
    pop di
    pop cx
    ret

;32位数转换成16进制数，参数是eax,si
long2hex:
    push eax
long2hex_loop:
    rol eax,8 ;先循环左移8位，低8位就是原来的高8位
    push eax
    and eax,0FFh ;做完与运算就可以当作char
    ;调用char2hex函数,但是与上面的char2hex有不同
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
    shl cl,1 ;使用cx来确定si的值
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
    inc i ;32位一共可以分解成4个char，所以循环做4次
    cmp i,4
    jne long2hex_loop
    mov i,0 ;最后赋予i和eax原值
    pop eax
    ret

main:
   mov ax, data
   mov ds, ax
   mov dx,offset input_info ;弹出提示输入信息
   mov ah,9
   int 21h
   call openfile ;调用打开文件函数
   mov ax,0B800h ;使用图形模式
   mov es,ax
;清屏
clear:
   mov cx,80*16
   mov ax,0020h
   cld
   rep stosw 
;文件大小
   mov ah,42h
   mov al,2
   mov bx,handle
   mov cx,0
   mov dx,0
   int 21h
   call filesize ;调用文件大小函数
   mov ax,word ptr offsets[2]
go:
   xor ax,ax
   xor cx,cx ;初始化
   mov ah,42h
   mov al,0  
   mov bx,handle
   mov cx,word ptr offsets[2]
   mov dx,word ptr offsets[0]
   int 21h ;将文件指针指向相应方位
   mov bx,word ptr file_size[0] 
   sub bx,word ptr offsets[0]
   cmp bx,256 ;文件总大小减偏移，小于256说明已到最后一页
   jb bytebuf
   mov word ptr byte_in_buf,256 ;大于256的话继续读入256个字节
   jmp nexts
bytebuf:
   mov word ptr byte_in_buf,bx ;最后一页的buf中字节数就是bx中的量
nexts:
   mov ah, 3Fh
   mov bx, handle
   mov cx, byte_in_buf
   mov dx, offset buf
   int 21h ;读文件
   mov eax,dword ptr offsets[0] ;eax的值为32位offsets数组的值
   mov di,0
   mov ecx,0
work:
   push eax
   call long2hex ;调用long2hex将偏移地址转成字符
   mov si,10 ;此时si移到数组的第10位
;将16进制数保存在s数组里
showhex:
   push si
   mov al,k 
   mov ah,0
   mov si,ax ;si的初始值用k来保存
   mov al,buf[si]
   pop si
   call char2hex ;调用char2hex来转换
   add j,1
   add k,1
   add si,2
   cmp si,56 ;用si的值来控制循环
   jb showhex
   mov si,59 ;移动数组到ascii码的首地址
   sub k,16 ;减去16重新读这16个数
   mov j,0
;将ascii码保存在数组中
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
   cmp j,16 ;用j来控制ascii码数量
   jne showascii
   xor si,si ;将si清零
;设置颜色
showcolor:
    mov ah,07h ;将所有字符设置为白色
    mov al,s[si]
    cmp al,'|'
    je color
    jne show
color: 
    mov ah,0Fh ;竖线设置为高亮度白色
show: 
    mov word ptr es:[di],ax
    inc si
    add di,2
    cmp si,75 ;一行最后的位置
    jb showcolor    
    add di,10 ;进入下一行
    pop eax
    add eax,16 ;一行显示16个数，偏移地址加16
    inc ecx
    mov j,0
    mov si,0
    cmp ecx,16 ;一共16行
    jb work 
    mov ah,1 ;显示
    int 21h
;按键
key:
    xor ax,ax ;将ax清零
    mov ah,0
    int 16h ;等待输入按键
    cmp ax,4900h ;前一页
    je pageup
    cmp ax,5100h ;后一页
    je pagedown
    cmp ax,4700h ;home键
    je home_jump
    cmp ax,4F00h ;end键
    je end_jump 
    cmp ax,011Bh ;ESC键
    je done_jump
    jmp go ;如果不是上述按键就跳回go，显示不变
;前一页
pageup:
    mov ax,word ptr offsets[0]
    sub ax,256 ;偏移量减256
    jc home_jump ;如果发生进位就相当于变成了home键
    mov word ptr offsets[0],ax ;赋予新的偏移量
    jmp go
end_jump:
    jmp endk ;由于跳间距太大，所以这是中转
done_jump:
    jmp done
home_jump:
    jmp home
;后一页
pagedown:
    mov ax,word ptr offsets[0]
    add ax,256 ;偏移量加256
    push ax
    sub ax,word ptr file_size[0] ;如果超过文件大小就相当于end键
    jnc endk
    pop ax
    mov word ptr offsets[0],ax
    jmp go
;home键
home:
    xor ax,ax 
    mov word ptr offsets[0],ax ;清零ax，赋给offsets
    jmp go
;end键
endk:
    mov ax,word ptr file_size[0] ;赋值文件大小
    and ax,0FFh ;如果与运算不为0，说明有单独行
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
   int 21h ;关闭文件
   mov ah,15
   int 10h
   mov ah,0
   int 10h ;清屏
   mov ah, 4Ch
   int 21h
code ends
end main
