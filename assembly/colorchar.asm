data segment
i db 0				
j dw 0				
s db 4 dup(0),0Dh,0Ah,'$';定义数组来存放转换的16进制数
data ends
code segment
assume cs:code,ds:data
main:
    mov ax,data
    mov ds,ax
    mov ax,0B800h;文本模式
    mov es,ax    
    mov cx,00FFh;最多有256个元素
    mov dl,0
    mov di,0 
    mov j,0
    mov i,25;列循环，一列25个元素
again:
    mov al,dl;显示dl对应的ASCII码
    mov ah,0Ch;颜色为亮红色
    mov word ptr es:[di],ax
    push ax
    push di
    push cx;将这三个寄存器保存至堆栈
change:
    mov ax,dx;把dl的值赋给al
    push dx
    mov cx,4;转换成16进制时的移位为4
    mov di,0;表示s数组下标
again2:
    push cx;保存cx的值，因为后面要对cx做减法，会改变此值
    mov cl,4
    rol ax,cl
    push ax
    and ax, 0Fh
    cmp ax, 10
    jb is_digit;如果ax值小于10则为数字，反之为字母
is_alpha:
    sub al, 10
    add al, 'A'
    jmp finish_4bits
is_digit:
    add al, '0'
finish_4bits:
    mov s[di], al;把al的值保存至数组中，256个元素最多只用两位来保存，所以保存在s[2],s[3]
    pop ax
    pop cx
    add di, 1
    sub cx, 1
    jnz again2
    mov al,s[2];分别用两个寄存器ax,bx来存储两位的16进制数
    mov ah,0Ah
    mov bl,s[3]
    mov bh,0Ah
    pop dx
    pop cx
    pop di;还原寄存器的值，di寄存器必须要在下一句之前还原，因为后面要用到di的值
    mov word ptr es:[di+2],ax
    mov word ptr es:[di+4],bx;在后面分别存储ax,bx的值，注意要+2,因为颜色字符各占一位
    pop ax
    add dl,1;ASCII码加1
    add di,160;为跳到下一行，di需要加160(一行有160位)
    sub cx,1;用cx的值来控制循环次数
    jz show
    sub i,1
    jnz again
next_row:
    add j,14;确定列间距
    mov di,j;确定下一列的起始位置
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