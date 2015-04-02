# DNSAnalyzer
Analysis of DNS traffic

The project attempts to analyze DNS Packets captured using Wireshark and sort and store them based on errors or delay in arrival of packets.

The project consists of the following classes:

Packet : This class extends the PcapPacket class (org.jnetpcap.packet.PcapPacket) and implements the Serializable interface.

PcapReader : This class consistes of methods that can read the .pcap file (obtained from Wireshark) and return lists of packets or specific packets.

DNSWrapper : This class consists of all the methods required to obtain various fields from the captured packet.


