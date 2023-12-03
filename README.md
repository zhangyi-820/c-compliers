##### 项目结构
###### test-files中为C语言测试代码
###### src中为项目代码
##### 联系方式（contact details）

email:qiushuang820@outlook.com

##### 项目介绍（project instraction）

这是一个用Java实现的C语言子集的编译器。语法分析方法采用LL(1)方法，能够实现将C语言的源代码翻译为四元式。目前能够实现变量的声明与使用、bool值语言、普通算术运算、for、while、if-else、if、do-while循环等语言的翻译。

This is a compiler for a subset of the C language implemented by Java. The grammatical analysis method adopts the LL(1) method, which can translate the source code of C language into quaternion. At present, it can realize the translation of variable declaration and use, bool value language, ordinary arithmetic operations, for, while, if-else, if, do-while loop and other languages.

##### 文法表达式

###### 开始语句（Start statement）

B->{S}

###### 复合语句（Compound statement）

S->CS

S->$

C->D | J | R

###### 控制语句（Control statement）

J-> if(E){S}else{S}

J->if(E){S}

J->while(E){S}

J->for(D;G;W){S}

###### bool运算(bool calculation)

E->HP

P->&&E | $

H->GI

I->||E

G->FKF | true |false

D-> < | > | == | != | <= | >=

G->(E)

G->!E

###### 变量声明语句(Variable declaration)

D-> TN

T->float|char|int|bool

N->ZA

Z->id U

A->,Z

U->=L | $

###### 赋值语句(Assignment statement)

R->id=L

###### 算术运算语句(arithmetic statement)

L->XM

M->+L | -L |$

X->FY

Y->*X | /x |$

F->(L)| id| num |bool 
