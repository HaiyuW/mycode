compile: client server

client: localclient.cpp
	g++ localclient.cpp -o localclient -lrt

server: localserver.cpp
	g++ localserver.cpp -o localserver -lrt

server_cpy: localserver_copy.cpp
	g++ localserver_copy.cpp -o localserver_copy -lrt

test_server: server
	./localserver

test_client: client
	./localclient

test_server_cpy: server_cpy
	./localserver_copy

test_client_asb: client.cpp
	g++ -lrt client.cpp -o client
	./client