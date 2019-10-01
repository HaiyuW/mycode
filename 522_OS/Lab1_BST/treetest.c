#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include <unistd.h>

#define BUF_LEN 128
typedef int Type;

// struct of BSTreeNode
typedef struct BSTreeNode
{
	Type key;
	char *value;
	struct BSTreeNode *left;
	struct BSTreeNode *right;
	struct BSTreeNode *parent;
	
}Node, *BSTree;

// create a Node
static Node* create_bstree_node(Type key, char *value, Node *parent, Node *left, Node *right); 
// In-order traversal and write to socket
void inorder_bstree(BSTree tree, int fd);
// insert a Node
static Node* bstree_insert(BSTree tree, Node *z);
// destroy a Node
void destroy_bstree(BSTree tree);
// get the number of each line
int get_number(char *str);

int main (int argc, char *args[])
{
	int sockfd;
	int port_number;
	struct sockaddr_in serverAdr;
	socklen_t clientAdr_size;
	char buf[1024];
	char ch = 'A';
	extern int errno;

	// create a socket
	if (argc !=2 )
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1)
	{
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// clear serverAdr and set it
	memset(&serverAdr, 0, sizeof(serverAdr));

	serverAdr.sin_family = AF_INET;

	// the ip address of "shell.cec.wustl.edu" is 128.252.167.161
	serverAdr.sin_addr.s_addr = inet_addr("127.0.0.1"); 

	// set port 2048
	port_number = atoi(args[1]);
	serverAdr.sin_port = htons(port_number);

	// connect to server
	if (connect(sockfd,(struct sockaddr *) &serverAdr,
			sizeof(serverAdr)) == -1){
		printf("Program error, reason: %s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	// 这个不用管，是我写的服务器的问题
	write(sockfd,&ch,1);
	sleep(1);

	FILE *fp = NULL;
	fp = fdopen(sockfd,"r");

	char str[BUF_LEN];
	char standard[BUF_LEN];
	int count = 0;
	memset(standard,0,BUF_LEN);

	BSTree root = NULL;

	// new a string array
	char *strArr[BUF_LEN];
	int i = 0;

	fgets(str,BUF_LEN,fp);
	while (strcmp(str,standard)!=0)
	{
		int str_len = strlen(str);
		// get number
		count = get_number(str);
		// allocate memory for each string
		strArr[i] = (char *)malloc(str_len*sizeof(char));
		strcpy(strArr[i],str);
		// create and insert a node
		Node *z = create_bstree_node(count,strArr[i],NULL,NULL,NULL);
		root = bstree_insert(root, z);
		i++;
		memset(str,0,BUF_LEN);
		fgets(str,BUF_LEN,fp);
	}

	// In-order traversal and it will provide right order
	inorder_bstree(root,sockfd);
	destroy_bstree(root);


	close(sockfd);
	return 0;

}

int get_number(char *str)
{
	int num=0;
	int len = strlen(str);
	int number;
	char c = ' ';
	int i;
	for (number=0;number<len;number++)
	{
		if (str[number]==c)
			break;
	}
	for (i=0;i<number;i++)
		num = num*10+str[i]-'0';
	return num;
}

static Node* create_bstree_node(Type key, char *value, Node *parent, Node *left, Node *right)
{
	Node* p;

	if ((p = (Node *)malloc(sizeof(Node))) == NULL)
		return NULL;

	p->key = key;
	p->value = value;
	p->left = left;
	p->right = right;
	p->parent = parent;
	return p;
}

void inorder_bstree(BSTree tree,int fd)
{
	int len;
	if (tree != NULL)
	{
		inorder_bstree(tree->left,fd);
		printf("%d ", tree->key);
		printf("%s", tree->value);
		len = strlen(tree->value);
		write(fd,tree->value,len);
		inorder_bstree(tree->right,fd);
	}
}


// insert and form a Binary Search Tree
static Node* bstree_insert(BSTree tree, Node *z)
{
	Node *y = NULL;
	Node *x = tree;

	while(x!=NULL)
	{
		y = x;
		if (z->key < x->key)
			x = x->left;
		else if (z->key > x->key)
			x = x->right; 
		else
		{
			free(z);
			return tree;
		}
	}

	z->parent = y;
	if (y==NULL)
		tree = z;
	else if (z->key<y->key)
		y->left = z;
	else
		y->right = z;

	return tree;
}

void destroy_bstree(BSTree tree)
{
	if (tree == NULL)
		return;

	if (tree->left!=NULL)
		destroy_bstree(tree->left);
	if (tree->right!=NULL)
		destroy_bstree(tree->right);

	free(tree);
}
