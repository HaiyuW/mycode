Name: Haiyu Wang
ID: 475533


When I tried to capture the packet,
I only used the display filter rather than the capture filter.

Please use “udp.port == 30123” at the display filter 
when checking ServerPacket.pcap and ClientPacket.pcap.

Also when testing these program please use following format:
java MapClient server_hostname 30123 <string>
e.g. java MapClient server_host name 30123 put:foo:bar
Note: the string should be with colon

I also upload a testScript_WHY. Please just modify the hostname and run it.