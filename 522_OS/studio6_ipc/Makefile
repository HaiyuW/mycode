all: compile_ipc compile_raw

test: compile_ipc test_signal test_pipe test_fifo test_local test_net

compile_ipc:
	gcc ipc.c -o ipc

compile_raw:
	gcc ipc_raw.c -o ipc_raw

test_signal:
	./ipc 100 signals

test_pipe:
	./ipc 100 pipe

test_fifo:
	./ipc 100 FIFO

test_local:
	./ipc 100 lsock

test_net:
	./ipc 100 socket