all: compile compile_server compile_client compile_poll compile_client_p compile_epoll

compile:
	gcc select.c -o select

compile_server:
	gcc server.c -o server

compile_client:
	gcc client.c -o client

compile_poll:
	gcc server_poll.c -o server_poll

compile_client_p:
	gcc client_poll.c -o client_poll

compile_epoll:
	gcc epoll.c -o epoll