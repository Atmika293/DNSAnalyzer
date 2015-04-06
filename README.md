# DNSAnalyzer
Analysis of DNS traffic

The project attempts to analyze DNS Packets captured using Wireshark and sort and store them based on errors or delay in arrival of packets.

Libraries used : jnetpcap , h2Database

The project consists of the following classes:

1.Packet : This class extends the PcapPacket class (org.jnetpcap.packet.PcapPacket) and implements the Serializable interface.

2.PcapReader : This class consists of methods that can read the .pcap file (obtained from Wireshark) and return lists of packets(instances of the class Packet) or specific packets.

3.PayloadRetriever : This class provides the method to obtain the DNS payload from the packet.

4.DNSWrapper : This class consists of all the methods required to obtain various fields from the DNS payload of the captured packet.

5.ProtocolStateMachine : This class contains the main method that builds the protocol state machine, creates the DB and tables required to store the packets and runs each packet through the machine.

The following classes are required for the construction of the Protocol State Machine:

a.State : This class represents a state in the machine.Each state is identified by a label(A,B..) and includes actions to be carried out when entering into the state(entry action), exiting from the state(exit action) and in the state(in-state action), in the form of methods.

b.Transition : This class represents a transition from one state to the next. Each transition has a start and an end state. The transition occurs between these two state. It implements the Observer interface and overrides the "update" method to update the begin and end state for each trasition and also execute the exit action of the start state and the entry action of the end state. 

c.TransitionSupport : This class represents the set of parameters to be passed to the update method of the corresponding transition. The parameters include the start state, the end state and the set of arguments (if any), along with their number, required to execute the "exit" action of the start state and the "entry" action of the end state. 

d.StateMachine : This class represent the protocol state machine. A state machine consists of a number of different states(instances of the class State): initial state("Initial"), end state("End") and intermediary states("A","B",..).it also defines a "current state", which could be assigned any of these states. It extends the Observable class and can be observed by any instance of a class that implements Observer, such as Transition. The state machine notifies the observer when there is a change in the current state,i.e.,current state is set to a new state.
