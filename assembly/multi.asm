.386
data1 segment use16
buffer db 25
       db ?
       db 25 dup(?);储存输入的第一个字符串
buff db 25
     db ?
     db 25 dup(?);储存输入的第二个字符串
i dw 0;储存第一个数字字符个数
j dw 0;储存第二个数字字符个数
a dw 0;储存第一个数
b dw 0;储存第二个数
higher dw 0;乘积的高16位
lower dw 0;乘积的低16位
s db 10 dup(0),0Dh,0Ah,'$';保存16进制的数组
k db 10 dup(' '),0Dh,0Ah,'$';保存10进制的数组
data1 ends

stack1 segment stack use16
dw 100h dup(?)
stack1 ends

code segment use16
assume cs:code, ds:data1, ss:stack1
start:
    mov ax,data1
    mov ds,ax
    mov ah,0Ah;调用10号功能保存键盘输入到缓存中的字符
    mov dx,offset buffer
    int 21h
    mov ah,2
    mov dl,0Dh;回车
    int 21h
    mov ah,2
    mov dl,0Ah;换行
    int 21h
    xor cx,cx;进行异或操作
    mov cl,buffer[1];保存字符个数
    ;jcxz done;如果未输入字符就直接结束
    mov i,cx;用i保存第一个字符个数
    mov cx,0
    mov bx,offset buffer+2;bx指向第一个字符
    mov ah,0Ah;第二个字符操作同上
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
    mov dl,[bx];以字符的形式一个个输出第一个字符串
    inc bx
    int 21h
    loop againa
output1:
    mov dl,'*';输出*号
    mov ah,02h
    int 21h
    mov cx,j
againb:
    mov ah,2
    mov dl,[di];输出第二个字符串
    inc di
    int 21h
    loop againb
output2:
    mov dl,'=';输出=号
    mov ah,02h
    int 21h
    mov ah,2
    mov dl,0Dh
    int 21h
    mov ah,2
    mov dl,0Ah;回车换行
    int 21h
;把两个字符串转化为数分别保存在a,b中
tranforma:
    mov bx,offset buffer+2
    mov ax,0
againa1:
    mov cx,0
    mov cl,buffer[bx];输入第一个字符
    mov si,10;10进制，所以每次乘10
    mul si
    sub cl,'0'
    add ax,cx
    inc bx
    sub i,1;用i表示位数来控制循环
    jnz againa1

;第二个字符操作同上
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
;进行乘法运算，高16位在dx，低16位在ax，用higher和lower来分别保存
cal:
    mov bx,a
    mul bx
    mov higher,dx
    mov lower,ax
;结果转化为10进制并输出
transformD:
    mov eax,higher;先给eax的低16位赋值higher
    shl eax,16;向左移动16位
    mov eax,lower;再给低16位赋值lower
    mov di,0;数组下标
    mov cx,0;统计push次数
againD:
    mov edx,0;32位除法，被除数是edx:eax
    mov ebx,10;除数ebx为0
    div ebx
    add dl,'0';转化成ASCII码push进栈
    push dx
    inc cx
    cmp eax,0;以eax为0结束循环
    jne againD
pop_againD:
    pop dx
    mov k[di],dl;把栈中数组pop出来保存进数组
    inc di
    dec cx;以cx为结束循环
    jnz pop_againD

    mov ah,9
    mov dx,offset k;显示数组保存的字符串
    int 21h

;16进制的输出
    mov cx,4
    mov di,0;s数组的下标
    mov bx,higher;先输出高16位
outputh:    
    push cx
    mov cl,4;每次循环左移4位
    rol bx,cl
    push bx
    and bx,000Fh
    cmp bx,10
    jb is_digit1
;如果是字符
is_alpha1:
    sub bl,10
    add bl,'A'
    jmp finish_4bits1
;如果是数字
is_digit1:
    add bl,'0'
finish_4bits1:
    mov s[di],bl;把高16位的数字保存到数组
    pop bx
    pop cx
    add di,1
    sub cx,1
    jnz outputh
    mov cx,4
    mov di,4
    mov bx,lower;进行低16位的数字的转化保存
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
    mov s[di],'h';在最后输出‘ h’
    mov ah,09h
    mov dx,offset s;输出字符串
    int 21h

;二进制的转换输出
    push ax
    mov bx,higher
    mov i,4;每个16位会有4组用i来控制
    mov cl,4;4位二进制数为一组
outputb1:
    rol bx,1;每次循环左移1位
    mov dl,0
    adc dl,'0';adc的是在add的基础上加上CF标志位的值
    mov ah,02h;显示字符
    int 21h
    sub cl,1
    jnz outputb1
space1:
    mov ah,02h;输出空格
    mov dl,' '
    int 21h
    mov cl,4
    sub i,1;
    jnz outputb1;跳回outputb1继续输出
    pop ax
;进行低16位的转换输出
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
    mov dl,' ';输出空格
    int 21h
    mov cl,4
    sub j,1
    jnz outputb2
    mov ah,02h
    mov dl,'B';输出B
    int 21h
    mov ah,2
    mov dl,0Dh
    int 21h
    mov ah,2
    mov dl,0Ah;回车换行
    int 21h
done:
    mov ah,4Ch
    int 21h
code ends
end start